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
    CommandLineRunner initDatabase(UserRepository repository) {
        return args -> {
            log.info("Preloading: " + repository.save(new User("ignorantalpaca@gmail.com",
                    Hashing.sha256().hashString("password", StandardCharsets.UTF_8).toString())));
            log.info("Preloading: " + repository.save(new User("ignorantalpaca@gmail.com",
                    Hashing.sha256().hashString("password", StandardCharsets.UTF_8).toString())));
        };
    }
}
