package com.planner.backend;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LoginAdvice {
    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String accountNotFoundHandler(AccountNotFoundException ex)
    {
        return ex.getMessage();
    }

    @ExceptionHandler(EmailInUseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String emailInUseHandler(EmailInUseException ex)
    {
        return ex.getMessage();
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    String incorrectPasswordHandler(IncorrectPasswordException ex)
    {
        return ex.getMessage();
    }
}
