package com.planner.backend;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

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

    @PostMapping("/login")
    Account login(@RequestBody Account login) {
        System.out.println("got new user: " + login.toString());
        login.setPasswordHash(new Hasher().hash(login.getPasswordHash()));

        Optional<Account> account = repository.findByEmail(login.getEmail());

        if (account.isPresent())
        {
            if (account.get().getPasswordHash().equals(login.getPasswordHash()))
            {
                // success
                Account result = account.get();
                result.setPasswordHash("");
                return result;
            }
            // wrong password
            throw new IncorrectPasswordException(login.getEmail());
        }

        // non-existent account
        throw new AccountNotFoundException(login.getEmail());
    }

    // this is just for testing
    @GetMapping("/accounts")
    List<Account> getAccounts() {
        return repository.findAll();
    }
}
