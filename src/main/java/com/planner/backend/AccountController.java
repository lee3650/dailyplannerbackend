package com.planner.backend;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class AccountController {
    private final AccountRepository repository;
    private LoginService loginService;
    private TemplateRepository templateRepository;

    AccountController(AccountRepository repository, LoginService loginService, TemplateRepository templateRepository)
    {
        this.repository = repository;
        this.loginService = loginService;
        this.templateRepository = templateRepository;
    }

    @PostMapping("/createAccount")
    Account newAccount(@RequestBody Account newUser) {
        System.out.println("got new user: " + newUser.toString());
        newUser.setPasswordHash(new Hasher().hash(newUser.getPasswordHash()));
        Optional<Account> existing = repository.findByEmail(newUser.getEmail());
        if (existing.isPresent())
        {
            throw new EmailInUseException(newUser.getEmail());
        }
        Template todayTemplate = new Template();
        todayTemplate.setName("today");
        todayTemplate.setEvents(new ArrayList<>());
        Account output = repository.save(newUser);
        todayTemplate.setOwner(output);
        output.setPasswordHash("");
        Template savedToday = templateRepository.save(todayTemplate);
        output.setTodayTemplate(savedToday.getId());
        return repository.save(output);
    }

    @PostMapping("/login")
    Account login(@RequestBody Account login) {
        System.out.println("logging in user: " + login.toString());
        return loginService.tryLogin(login);
    }

    // this is just for testing
    @GetMapping("/accounts")
    List<Account> getAccounts() {
        return repository.findAll();
    }
}
