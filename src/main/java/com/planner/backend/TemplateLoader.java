package com.planner.backend;

import java.util.Optional;

public class TemplateLoader {
    public static Template loadTemplate(TemplateRepository repo, Account account, long id)
    {
        if (id == -1)
        {
            id = account.getTodayTemplate();
        }

        Optional<Template> option = repo.findById(id);
        if (option.isEmpty())
        {
            throw new TemplateNotFoundException(id);
        }

        if (!option.get().getOwner().getId().equals(account.getId()))
        {
            throw new UnauthorizedAccessException("Template with id " + id);
        }

        return option.get();
    }
}

