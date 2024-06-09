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

        if (id == -1)
        {
            Optional<Template> optionalToday = templateRepository.findById(login.getTodayTemplate());
            if (optionalToday.isPresent())
            {
                return optionalToday.get();
            }
            else
            {
                // that's not good lol
                throw new RuntimeException("Somehow the today template didn't exist for user " + login.getId());
            }
        }

        Optional<Template> possibleResult = templateRepository.findById(id);
        if (possibleResult.isPresent())
        {
            Template result = possibleResult.get();
            if (result.getOwner().getId().equals(login.getId()))
            {
                return result;
            }
            throw new UnauthorizedAccessException("template of id " + result.getId());
        }

        throw new TemplateNotFoundException(id);
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
    public Template loadIntoToday(@RequestHeader("auth") String authStr, @PathVariable long templateId)
    {
        Account login = loginService.tryLogin(authStr);

        Optional<Template> optToday = templateRepository.findById(login.getTodayTemplate());

        if (optToday.isEmpty())
        {
            throw new TemplateNotFoundException(login.getTodayTemplate());
        }

        Optional<Template> optionalTemplate = templateRepository.findById(templateId);
        if (optionalTemplate.isEmpty())
        {
            throw new TemplateNotFoundException(templateId);
        }

        Template today = optToday.get();
        Template clone = optionalTemplate.get();

        today.setEvents(new ArrayList<>());

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

    @PostMapping("/writeTemplate")
    public Template writeTemplate(@RequestBody Template request, @RequestHeader("auth") String authStr) {
        Account login = loginService.tryLogin(authStr);

        request.setOwner(login);

        // so, I guess if this template already exists, we update it, otherwise we um, will create it...
        // it's new if the request.templateId is null.

        if (request.getId() == null)
        {
            // new. Just add and create
            login.getTemplates().add(request);
            Template added = templateRepository.save(request);
            accountRepository.save(login);
            return added;
        }

        // write to the today template
        if (request.getId() == -1)
        {
            request.setId(login.getTodayTemplate());
            return templateRepository.save(request);
        }

        // existing. Just return.
        for (int i = 0; i < login.getTemplates().size(); i++)
        {
            if (login.getTemplates().get(i).getId().equals(request.getId()))
            {
                login.getTemplates().set(i, request);
                break;
            }
        }

        Template result = templateRepository.save(request);
        accountRepository.save(login);
        return result;
    }
}
