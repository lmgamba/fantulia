package com.fantulia.backend.config;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// @Component: registers this class as a Spring bean. Spring Boot adds every
// Filter bean to the servlet filter chain on its own, so no extra registration
// code is needed.
//
// Why a servlet filter and not an MVC interceptor: a filter wraps the whole
// request, including routes with no handler and error responses, so a 404
// ProblemDetail under /api gets the header too. An interceptor only runs for
// requests that reach a mapped handler.
//
// Why at all: API responses are per-visitor and must never be stored by a
// browser, CDN or proxy. Cloudflare does not cache JSON by default, but relying
// on that default would silently break the day login cookies exist (v0.2).
@Component
public class ApiNoStoreFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        filterChain.doFilter(request, response);
    }
}
