package com.bajaj.qualifier.startup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.bajaj.qualifier.service.WebhookService;

@Component
public class StartupRunner implements CommandLineRunner {

    private final WebhookService webhookService;

    public StartupRunner(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Override
    public void run(String... args) {
    	System.out.println("stratup runner executed");
         webhookService.executeFlow();
    }
}

