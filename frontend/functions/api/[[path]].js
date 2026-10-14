// Cloudflare Pages Function: forwards every /api/* request from the public site
// to the API running on the VM.
//
// Why it exists: the browser only ever talks to one origin (fantulia.com), so
// the API needs no CORS configuration. The VM is reached through a separate,
// browser-invisible hostname set in API_ORIGIN.
//
// Why the extra headers: Caddy on the VM rejects requests without
// X-Origin-Secret, so the API can only be reached through this function, and
// that is what makes the client IP below trustworthy for rate limiting.

export async function onRequest({ request, env, params }) {
  if (!env.API_ORIGIN || !env.ORIGIN_SECRET) {
    // Generic on purpose: do not reveal which setting is missing.
    return problem(503, 'API unavailable');
  }

  let target;
  try {
    const segments = [].concat(params.path ?? []);
    target = new URL(`/api/${segments.join('/')}`, env.API_ORIGIN);
    target.search = new URL(request.url).search;
    // The path is always rebuilt under /api/ on the configured origin, so this
    // function can never be used as an open proxy. The check also catches
    // encoded ".." segments that would climb out of /api/.
    if (target.origin !== new URL(env.API_ORIGIN).origin || !target.pathname.startsWith('/api/')) {
      return problem(400, 'Bad request');
    }
  } catch {
    return problem(503, 'API unavailable');
  }

  const upstream = new Request(target, request);
  // Cookie, Content-Type, X-Attempt-Token and the rest pass through unchanged.
  // Forwarding headers a visitor could have sent themselves are replaced, never trusted.
  upstream.headers.delete('Host');
  upstream.headers.delete('X-Real-IP');
  upstream.headers.delete('Forwarded');
  upstream.headers.delete('X-Forwarded-For');
  const clientIp = request.headers.get('CF-Connecting-IP');
  if (clientIp) {
    upstream.headers.set('X-Forwarded-For', clientIp);
  }
  upstream.headers.set('X-Origin-Secret', env.ORIGIN_SECRET);

  try {
    // Redirects and Set-Cookie come back untouched: the browser, not this
    // function, decides what to do with them.
    return await fetch(new Request(upstream, { redirect: 'manual' }));
  } catch {
    return problem(502, 'API unreachable');
  }
}

// Same RFC 9457 shape the API itself uses for errors, so the frontend handles
// one error format.
function problem(status, title) {
  return new Response(JSON.stringify({ title, status }), {
    status,
    headers: { 'Content-Type': 'application/problem+json', 'Cache-Control': 'no-store' },
  });
}
