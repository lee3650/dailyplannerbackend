package com.planner.backend;

public class IncorrectPasswordException extends RuntimeException
{
    IncorrectPasswordException(String email) {
        super("Incorrect password for user " + email);
    }
}
