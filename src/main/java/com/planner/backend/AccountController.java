package com.planner.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountController {
    private final AccountRepository repository;

    AccountController(AccountRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/createAccount")
    Account newAccount(@RequestBody Account newUser) {
        System.out.println("got new user: " + newUser.toString());
        newUser.setPasswordHash(new Hasher().hash(newUser.getPasswordHash()));
        Account output = repository.save(newUser);
        output.setPasswordHash("");
        return output;
    }

    // this is just for testing
    @GetMapping("/accounts")
    List<Account> getAccounts() {
        return repository.findAll();
    }
}
