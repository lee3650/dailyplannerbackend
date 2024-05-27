package com.planner.backend;

import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(AccountRepository repository, TemplateRepository templateRepository) {
        return args -> {
            Account first = repository.save(new Account("testemail1@gmail.com",
                    new Hasher().hash("password")));
            log.info("Preloading: " + first);
            log.info("Preloading: " + repository.save(new Account("testemail2@gmail.com",
                    new Hasher().hash("password"))));
            Template template = new Template();
            template.setOwner(first);
            EventData event = new EventData(0, 60, template);
            ArrayList<EventData> events = new ArrayList<>();
            events.add(event);
            template.setEvents(events);
            log.info("Preloading: " + templateRepository.save(template));
        };
    }
}
