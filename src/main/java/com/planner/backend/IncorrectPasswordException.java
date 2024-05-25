package com.planner.backend;

public class IncorrectPasswordException extends RuntimeException
{
    IncorrectPasswordException(String email) {
        super("Login failed for user with email " + email);
    }
}
