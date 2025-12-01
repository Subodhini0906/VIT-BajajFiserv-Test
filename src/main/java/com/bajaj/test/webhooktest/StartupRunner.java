package com.bajaj.test.webhooktest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    private final WebhookService service;

    public StartupRunner(WebhookService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) throws Exception {
        service.startProcess();
    }
}
