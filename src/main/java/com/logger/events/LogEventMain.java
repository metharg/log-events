package com.logger.events;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.logger.events.service.LogEventService;

@SpringBootApplication
public class LogEventMain implements CommandLineRunner {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(LogEventMain.class);

    @Autowired
    private LogEventService service;

    public static void main(String... args) {
        SpringApplication app = new SpringApplication(LogEventMain.class);
        app.run(args);
    }

    @Override
    public void run(String... args) {
        Instant start = Instant.now();
        service.execute(args);
        Instant end = Instant.now();
        LOGGER.info("Total time: {}ms", Duration.between(start, end).toMillis());
    }
}
