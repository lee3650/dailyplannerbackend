package com.planner.backend;

import com.sun.jdi.request.InvalidRequestStateException;
import jakarta.persistence.PostPersist;
import org.springframework.web.bind.annotation.*;

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
    public Template readTemplate(@PathVariable long id, @RequestHeader("userId") long userId, @RequestHeader("password") String password) {
        Account login = loginService.tryLogin(userId, password);
        // well, that's it I guess... don't we have unique IDs for all templates?
        // eh, true.

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
    public List<Template> readTemplates(@RequestBody Account account)
    {
        Account login = loginService.tryLogin(account);
        return login.getTemplates();
    }

    @PostMapping("/writeTemplates")
    public void writeTemplates(@RequestBody WriteTemplatesRequest request)
    {
        Account login = loginService.tryLogin(request.account);

        for (Template t : request.templates)
        {
            t.setOwner(login);
        }

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

    public static class WriteTemplateRequest {
        private Template template;
        private Account account;

        public WriteTemplateRequest()
        {

        }

        public Template getTemplate() {
            return template;
        }

        public void setTemplate(Template template) {
            this.template = template;
        }

        public Account getAccount() {
            return account;
        }

        public void setAccount(Account account) {
            this.account = account;
        }
    }

    @PostMapping("/writeTemplate")
    public Template writeTemplate(@RequestBody WriteTemplateRequest request) {
        if (request.account == null || request.template == null)
        {
            throw new InvalidRequestStateException("Invalid request state for template write!");
        }

        Account login = loginService.tryLogin(request.account);
        request.template.setOwner(login);

        // so, I guess if this template already exists, we update it, otherwise we um, will create it...
        // it's new if the request.templateId is null.

        if (request.template.getId() == null)
        {
            // new. Just add and create
            login.getTemplates().add(request.template);
            Template added = templateRepository.save(request.template);
            accountRepository.save(login);
            return added;
        }

        // existing. Just return.
        for (int i = 0; i < login.getTemplates().size(); i++)
        {
            if (login.getTemplates().get(i).getId().equals(request.template.getId()))
            {
                login.getTemplates().set(i, request.template);
                break;
            }
        }

        Template result = templateRepository.save(request.template);
        accountRepository.save(login);
        return result;
    }
}
