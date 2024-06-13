package com.planner.backend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class TemplateControllerUnitTests {
    @Autowired
    TemplateController controller;

    @Autowired
    AccountController accountController;

    @AfterEach
    void cleanup()
    {
        accountController.clearDatabase();
    }

    @Test
    void Can_Create_Template()
    {
        Account newAccount = accountController.newAccount(new Account("test", "test"));
        TemplateController.AddTemplateRequest request = new TemplateController.AddTemplateRequest();
        request.name = "new template";
        Template result = controller.addTemplate(String.format("%d:test", newAccount.getId()), request);
        assertEquals("new template", result.getName());
        assertEquals(0, result.getEvents().size());
        assertEquals(newAccount.getId(), result.getOwner().getId());
    }

    @Test
    void Can_Delete_Template()
    {
        Account newAccount = accountController.newAccount(new Account("test", "test"));
        TemplateController.AddTemplateRequest request = new TemplateController.AddTemplateRequest();
        request.name = "new template";
        Template template = controller.addTemplate(String.format("%d:test", newAccount.getId()), request);
        List<Template> templates = controller.deleteTemplate("%d:test".formatted(newAccount.getId()), template.getId());
        assertEquals(0, templates.size());
        assertThrows(TemplateNotFoundException.class, () -> {
           controller.readTemplate(template.getId(), "%d:test".formatted(newAccount.getId()));
        });
    }
}
