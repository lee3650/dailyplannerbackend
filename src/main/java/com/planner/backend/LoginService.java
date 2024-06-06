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

    public Account tryLogin(long userId, String password)
    {
        String hash = new Hasher().hash(password);

        Optional<Account> account = repository.findById(userId);
        if (account.isPresent())
        {
            if (account.get().getPasswordHash().equals(hash))
            {
                // success
                Account result = account.get();
                return result;
            }

            // wrong password
            throw new IncorrectPasswordException("" + userId);
        }

        // non-existent account
        throw new AccountNotFoundException("" + userId);
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
                return result;
            }

            // wrong password
            throw new IncorrectPasswordException(login.getEmail());
        }

        // non-existent account
        throw new AccountNotFoundException(login.getEmail());
    }
}
