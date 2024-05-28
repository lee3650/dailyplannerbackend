package com.planner.backend;

public class TemplateNotFoundException extends RuntimeException {
    TemplateNotFoundException(Long id)
    {
        super("Could not find Template with id " + id);
    }
}
