package com.planner.backend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventDataWebTests {
    @Autowired
    TestRestTemplate template;

    @Autowired
    AccountController controller;

    @AfterEach
    void cleanup()
    {
        controller.clearDatabase();
        // TODO - cleanup the event data repository
    }

    @Test
    void Can_Add_Event_To_Template()
    {
        Account created = template.postForObject("/createAccount", new Account("test", "test"), Account.class);
        HttpHeaders headers = new HttpHeaders();
        headers.set("auth", "%d:test".formatted(created.getId()));
        TemplateController.AddTemplateRequest addRequest = new TemplateController.AddTemplateRequest();
        HttpEntity<TemplateController.AddTemplateRequest> requestEntity = new HttpEntity<>(addRequest, headers);
        addRequest.name = "new template";
        Template newTemplate = template.postForObject("/addTemplate", requestEntity, Template.class);
        EventDataController.EventDataRequest eventBody = new EventDataController.EventDataRequest();
        eventBody.templateId = newTemplate.getId();
        eventBody.eventData = new EventData(60, 120, "my name", null);
        HttpEntity<EventDataController.EventDataRequest> eventRequest = new HttpEntity<>(eventBody, headers);
        Template result = template.postForObject("/addEvent", eventRequest, Template.class);

        assertEquals(1, result.getEvents().size());
        assertEquals("my name", result.getEvents().get(0).getName());
        assertEquals(60, result.getEvents().get(0).getStartTime());
        assertEquals(120, result.getEvents().get(0).getEndTime());
    }
}
