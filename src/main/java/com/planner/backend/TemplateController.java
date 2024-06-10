package com.planner.backend;

import com.sun.jdi.request.InvalidRequestStateException;
import jakarta.persistence.PostPersist;
import org.springframework.web.bind.annotation.*;

import javax.annotation.RegEx;
import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class TemplateController {
    private LoginService loginService;
    private TemplateRepository templateRepository;
    private AccountRepository accountRepository;

    public TemplateController(LoginService loginService, TemplateRepository templateRepository, AccountRepository accountRepository)
    {
        this.loginService = loginService;
        this.templateRepository = templateRepository;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/readTemplate/{id}")
    public Template readTemplate(@PathVariable long id, @RequestHeader("auth") String authStr) {
        Account login = loginService.tryLogin(authStr);
        // well, that's it I guess... don't we have unique IDs for all templates?
        // eh, true.

        return TemplateLoader.loadTemplate(templateRepository, login, id);
    }

    @GetMapping("/readTemplates")
    public List<Template> readTemplates(@RequestHeader("auth") String authStr)
    {
        Account login = loginService.tryLogin(authStr);
        return new TemplatesCleaner().CleanTemplates(login);
    }

    public static class AddTemplateRequest
    {
        public String name;

        public AddTemplateRequest()
        {

        }
    }

    @PostMapping("/loadIntoToday/{id}")
    public Template loadIntoToday(@RequestHeader("auth") String authStr, @PathVariable long id)
    {
        Account login = loginService.tryLogin(authStr);

        Optional<Template> optToday = templateRepository.findById(login.getTodayTemplate());

        if (optToday.isEmpty())
        {
            throw new TemplateNotFoundException(login.getTodayTemplate());
        }

        if (id == login.getTodayTemplate())
        {
            return optToday.get();
        }

        Optional<Template> optionalTemplate = templateRepository.findById(id);
        if (optionalTemplate.isEmpty())
        {
            throw new TemplateNotFoundException(id);
        }

        Template today = optToday.get();
        Template clone = optionalTemplate.get();

        today.getEvents().clear();
        templateRepository.save(today);

        for (EventData d : clone.getEvents())
        {
            today.getEvents().add(d.clone(today));
        }

        return templateRepository.save(today);
    }

    @PostMapping("/addTemplate")
    public Template addTemplate(@RequestHeader("auth") String authStr, @RequestBody AddTemplateRequest request)
    {
        Account login = loginService.tryLogin(authStr);

        Template added = new Template();
        added.setOwner(login);
        added.setName(request.name);
        added.setEvents(new ArrayList<>());

        return templateRepository.save(added);
    }

    @DeleteMapping("/deleteTemplate/{id}")
    public List<Template> deleteTemplate(@RequestHeader("auth") String authStr, @PathVariable long id)
    {
        Account login = loginService.tryLogin(authStr);

        if (login.getTodayTemplate().equals(id))
        {
            throw new RuntimeException("Bad request: Can't delete the 'today' template!");
        }

        for (int i = 0; i < login.getTemplates().size(); i++)
        {
            if (login.getTemplates().get(i).getId() == id)
            {
                // remove this one
                login.getTemplates().remove(i);
                break;
            }
        }

        templateRepository.deleteById(id);
        Account saved = accountRepository.save(login);
        return new TemplatesCleaner().CleanTemplates(saved);
    }

    @PostMapping("/writeTemplates")
    public void writeTemplates(@RequestBody WriteTemplatesRequest request)
    {
        Account login = loginService.tryLogin(request.account);

        for (Template t : request.templates)
        {
            t.setOwner(login);
        }

        Optional<Template> optToday = templateRepository.findById(login.getTodayTemplate());
        if (optToday.isEmpty())
        {
            throw new TemplateNotFoundException(login.getTodayTemplate());
        }

        request.templates.add(optToday.get());

        login.setTemplates(request.templates);
        accountRepository.save(login);
    }

    public static class WriteTemplatesRequest {
        public List<Template> templates;
        public Account account;

        public WriteTemplatesRequest() { }

        public WriteTemplatesRequest(List<Template> templates) {
            this.templates = templates;
        }
    }

    @PostMapping("/renameTemplate/{id}")
    public Template writeTemplate(@PathVariable long id, @RequestBody String newName, @RequestHeader("auth") String authStr) {
        Account login = loginService.tryLogin(authStr);
        Template loaded = TemplateLoader.loadTemplate(templateRepository, login, id);
        loaded.setName(newName);
        return templateRepository.save(loaded);
    }
}
