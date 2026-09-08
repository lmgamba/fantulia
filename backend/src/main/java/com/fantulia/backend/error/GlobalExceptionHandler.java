package com.fantulia.backend.error;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// @RestControllerAdvice: a @ControllerAdvice (applies across every
// @RestController, not just one) combined with @ResponseBody, so the
// ProblemDetail objects this class produces are written straight to the
// response body. This is the single place every exception in the app gets
// turned into a client-facing response — the project rule is that stack
// traces, SQL text, and fully-qualified class names never reach a client,
// and centralizing that mapping here is what makes it enforceable.
//
// Extending ResponseEntityExceptionHandler is the framework-recommended way
// to do this: its default method implementations already build an RFC 9457
// ProblemDetail (type/title/status/detail) for standard Spring MVC
// exceptions — including NoResourceFoundException (404, unmapped route) and
// MethodArgumentNotValidException (400, @Valid failures) — without needing
// to override anything yet. Future features add their own @ExceptionHandler
// methods here for domain-specific errors (e.g. 409 conflicts).
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
}
