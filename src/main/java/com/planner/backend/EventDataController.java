package com.planner.backend;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.Option;
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

    public static class EventDataRequest
    {
        public EventData eventData;
        public Long templateId;

        public EventDataRequest()
        {

        }
    }

    @PostMapping("/addEvent")
    public Template addEvent(@RequestHeader("auth") String authStr, @RequestBody EventDataRequest request)
    {
        // TODO - handle the "today" template

        Account login = loginService.tryLogin(authStr);

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

    @PostMapping("/updateEvent")
    public Template updateEvent(@RequestHeader("auth") String authStr, @RequestBody EventDataRequest request)
    {
        Account login = loginService.tryLogin(authStr);

        Optional<Template> optionalTemplate = templateRepository.findById(request.templateId);
        if (optionalTemplate.isEmpty())
        {
            throw new TemplateNotFoundException(request.templateId);
        }

        Template template = optionalTemplate.get();
        Optional<EventData> optionalEventData = eventDataRepository.findById(request.eventData.id);
        if (optionalEventData.isEmpty())
        {
            throw new RuntimeException("Tried to update invalid event data id " + request.eventData.id);
        }

        EventData data = optionalEventData.get();

        data.setTemplate(template);
        data.setStartTime(request.eventData.getStartTime());
        data.setEndTime(request.eventData.getEndTime());
        data.setName(request.eventData.getName());

        EventData saved = eventDataRepository.save(data);
        Template savedTemplate = templateRepository.save(template);
        return savedTemplate;
    }
}
