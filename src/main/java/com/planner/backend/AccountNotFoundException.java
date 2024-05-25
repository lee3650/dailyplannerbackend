package com.planner.backend;

public class AccountNotFoundException extends RuntimeException {
    AccountNotFoundException(String email)
    {
        super("Could not find user with email " + email);
    }
}
