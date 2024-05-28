package com.planner.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class TemplateController {
    private LoginService loginService;
    private TemplateRepository templateRepository;

    public TemplateController(LoginService loginService, TemplateRepository templateRepository)
    {
        this.loginService = loginService;
        this.templateRepository = templateRepository;
    }

    @GetMapping("/readTemplate/{id}")
    public Template readTemplate(@PathVariable long id, @RequestBody Account account) {
        Account login = loginService.tryLogin(account);
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
}
