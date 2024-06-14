package com.planner.backend;

import org.springframework.web.bind.annotation.*;

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

    public void clearDatabase()
    {
        eventDataRepository.deleteAll();
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
        Account login = loginService.tryLogin(authStr);
        Template template = TemplateLoader.loadTemplate(templateRepository, login, request.templateId);
        request.eventData.setTemplate(template);
        EventData saved = eventDataRepository.save(request.eventData);
        template.getEvents().add(saved);
        Template savedTemplate = templateRepository.save(template);
        return savedTemplate;
    }

    @DeleteMapping("/deleteEvent/{id}")
    public Template deleteEvent(@PathVariable long id, @RequestHeader("auth") String authStr)
    {
        Account login = loginService.tryLogin(authStr);

        Optional<EventData> optionalData = eventDataRepository.findById(id);
        if (optionalData.isEmpty())
        {
            throw new RuntimeException("Couldn't find event data with id " + id);
        }

        EventData data = optionalData.get();
        Template template = TemplateLoader.loadTemplate(templateRepository, login, data.getTemplate().getId());

        for (int i = 0; i < template.getEvents().size(); i++)
        {
            if (template.getEvents().get(i).getId().equals(id))
            {
                template.getEvents().remove(i);
                break;
            }
        }

        return templateRepository.save(template);
    }

    @PostMapping("/updateEvent")
    public Template updateEvent(@RequestHeader("auth") String authStr, @RequestBody EventDataRequest request)
    {
        Account login = loginService.tryLogin(authStr);

        Template template = TemplateLoader.loadTemplate(templateRepository, login, request.templateId);
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
