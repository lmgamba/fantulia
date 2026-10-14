// Tests for functions/api/[[path]].js using Node's built-in test runner (no extra
// dependency). They live outside functions/ because Cloudflare turns every file
// in that folder into a route and would try to bundle this one.
import assert from 'node:assert/strict';
import { afterEach, beforeEach, describe, it, mock } from 'node:test';
import { onRequest } from '../functions/api/[[path]].js';

const env = { API_ORIGIN: 'https://origin.example.test', ORIGIN_SECRET: 's3cret' };

describe('/api forwarding function', () => {
  let seen; // the Request the function sent to the origin

  beforeEach(() => {
    seen = undefined;
    mock.method(globalThis, 'fetch', async (req) => {
      seen = req;
      return new Response('{"status":"ok"}', {
        status: 201,
        headers: { 'Set-Cookie': 'sid=1; HttpOnly', 'Cache-Control': 'no-store' },
      });
    });
  });
  afterEach(() => mock.restoreAll());

  it('forwards to the fixed /api path on the origin with the secret and the real client IP', async () => {
    const request = new Request('https://fantulia.com/api/attempts/abc/answers?x=1', {
      method: 'POST',
      body: '{"chosen_index":2}',
      headers: {
        'Content-Type': 'application/json',
        Cookie: 'sid=1',
        'X-Attempt-Token': 'tok',
        'X-Forwarded-For': '6.6.6.6', // spoof attempt
        'X-Origin-Secret': 'guess', // spoof attempt
        'CF-Connecting-IP': '203.0.113.7',
      },
    });

    await onRequest({ request, env, params: { path: ['attempts', 'abc', 'answers'] } });

    assert.equal(seen.url, 'https://origin.example.test/api/attempts/abc/answers?x=1');
    assert.equal(seen.method, 'POST');
    assert.equal(await seen.text(), '{"chosen_index":2}');
    assert.equal(seen.headers.get('X-Origin-Secret'), 's3cret');
    assert.equal(seen.headers.get('X-Forwarded-For'), '203.0.113.7');
    assert.equal(seen.headers.get('Cookie'), 'sid=1');
    assert.equal(seen.headers.get('X-Attempt-Token'), 'tok');
  });

  it('returns the origin response untouched, including status and Set-Cookie', async () => {
    const request = new Request('https://fantulia.com/api/health');
    const response = await onRequest({ request, env, params: { path: ['health'] } });

    assert.equal(response.status, 201);
    assert.equal(response.headers.get('Set-Cookie'), 'sid=1; HttpOnly');
    assert.equal(response.headers.get('Cache-Control'), 'no-store');
    assert.equal(await response.text(), '{"status":"ok"}');
  });

  it('never trusts a visitor-supplied forwarding header when there is no client IP', async () => {
    const request = new Request('https://fantulia.com/api/health', {
      headers: { 'X-Forwarded-For': '6.6.6.6' },
    });
    await onRequest({ request, env, params: { path: ['health'] } });

    assert.equal(seen.headers.get('X-Forwarded-For'), null);
  });

  it('refuses paths that would leave /api/ and never calls the origin', async () => {
    const request = new Request('https://fantulia.com/api/x');
    const response = await onRequest({ request, env, params: { path: ['..', 'secret'] } });

    assert.equal(response.status, 400);
    assert.equal(seen, undefined);
  });

  it('answers 503 problem+json without calling the origin when it is not configured', async () => {
    const request = new Request('https://fantulia.com/api/health');
    const response = await onRequest({ request, env: {}, params: { path: ['health'] } });

    assert.equal(response.status, 503);
    assert.equal(response.headers.get('Content-Type'), 'application/problem+json');
    assert.equal(seen, undefined);
  });

  it('answers 502 problem+json when the origin cannot be reached', async () => {
    mock.method(globalThis, 'fetch', async () => {
      throw new TypeError('fetch failed');
    });
    const request = new Request('https://fantulia.com/api/health');
    const response = await onRequest({ request, env, params: { path: ['health'] } });

    assert.equal(response.status, 502);
    assert.equal(response.headers.get('Content-Type'), 'application/problem+json');
  });
});
