package com.planner.backend;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class EventDataController {
    private EventDataRepository eventDataRepository;
    private TemplateRepository templateRepository;
    private LoginService loginService;

    public EventDataController(EventDataRepository repository, LoginService loginService, TemplateRepository templateRepository)
    {
        this.eventDataRepository = repository;
        this.loginService = loginService;
        this.templateRepository = templateRepository;
    }

    public class EventDataRequest
    {
        public EventData eventData;
        public Long templateId;

        public EventDataRequest()
        {

        }
    }

    @PostMapping("/addEvent")
    public Template addEvent(@RequestHeader("userId") long userId, @RequestHeader("password") String password, @RequestBody EventDataRequest request)
    {
        // TODO - handle the "today" template

        Account login = loginService.tryLogin(userId, password);

        Optional<Template> optionalTemplate = templateRepository.findById(request.templateId);
        if (optionalTemplate.isEmpty())
        {
            throw new TemplateNotFoundException(request.templateId);
        }

        Template template = optionalTemplate.get();
        request.eventData.setTemplate(template);
        EventData saved = eventDataRepository.save(request.eventData);
        template.getEvents().add(saved);
        Template savedTemplate = templateRepository.save(template);
        return savedTemplate;
    }
}
