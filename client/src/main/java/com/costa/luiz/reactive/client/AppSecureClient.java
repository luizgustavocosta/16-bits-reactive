package com.costa.luiz.reactive.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class AppSecureClient {

    public static void main(String[] args) {
        SpringApplication.run(AppSecureClient.class, args);
    }

    @Bean
    @Profile("secure")
    WebClient secureWebClient(WebClient.Builder builder) {
        return builder
                .filter(ExchangeFilterFunctions.basicAuthentication("luiz", "costa"))
                .build();
    }
}

@Component
@Log4j2
@RequiredArgsConstructor
@Profile("secure")
class SecureConsumer {

    private final WebClient secureWebClient;

    @EventListener(ApplicationReadyEvent.class)
    public void ready() {
        this.secureWebClient.get()
                .uri("http://localhost:8080/secure/greetings")
                .retrieve()
                .bodyToFlux(GreetingResponse.class)
                .subscribe(log::info);
    }
}