package com.planner.backend;

public class UnauthorizedAccessException extends RuntimeException {
    UnauthorizedAccessException(String resource)
    {
        super("Unauthorized attempt to access resource " + resource);
    }
}
