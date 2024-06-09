package com.planner.backend;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TemplatesCleaner {
    public List<Template> CleanTemplates(Account account)
    {
        ArrayList<Template> result = new ArrayList<>();
        for (Template t : account.getTemplates())
        {
            if (!t.getId().equals(account.getTodayTemplate()))
            {
                result.add(t);
            }
        }
        return result;
    }

    public Account CleanTemplateAccount(Account account)
    {
        List<Template> cleaned = CleanTemplates(account);
        account.setTemplates(cleaned);
        return account;
    }
}
