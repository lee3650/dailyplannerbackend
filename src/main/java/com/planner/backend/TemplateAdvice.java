package com.planner.backend;

import com.sun.jdi.request.InvalidRequestStateException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TemplateAdvice {
    @ExceptionHandler(TemplateNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String templateNotFoundHandler(TemplateNotFoundException ex)
    {
        return ex.getMessage();
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    String unauthorizedRequest(UnauthorizedAccessException ex)
    {
        return ex.getMessage();
    }

    @ExceptionHandler(InvalidRequestStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String invalidRequestHandler(InvalidRequestStateException ex)
    {
        return ex.getMessage();
    }
}
