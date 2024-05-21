package com.planner.backend;

import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(AccountRepository repository) {
        return args -> {
            log.info("Preloading: " + repository.save(new Account("testemail1@gmail.com",
                    new Hasher().hash("password"))));
            log.info("Preloading: " + repository.save(new Account("testemail2@gmail.com",
                    new Hasher().hash("password"))));
        };
    }
}
