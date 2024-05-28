package com.planner.backend;

import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class LoginService {
    private AccountRepository repository;

    public LoginService(AccountRepository repository)
    {
        this.repository = repository;
    }

    public Account tryLogin(Account login) {
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
}
