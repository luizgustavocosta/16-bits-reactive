package com.costa.luiz.reactive.client;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class AppRSocketClient {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(AppRSocketClient.class, args);
    }
    @Bean
    RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
        return builder.tcp("localhost", 7_000);
    }
}

@Component
@RequiredArgsConstructor
@Log4j2
@Profile("!secure")
class RSocketConsumer {

    private final RSocketRequester rSocketRequester;

    @EventListener(ApplicationReadyEvent.class)
    public void consume() {
        this.rSocketRequester
                .route("greetings.{timeInSeconds}", 2)
                .data(new GreetingRequest("Luiz"))
                .retrieveFlux(GreetingResponse.class)
                .subscribe(log::info);

    }
}


@Data
@NoArgsConstructor
@AllArgsConstructor
class GreetingRequest {
    private String name;

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class GreetingResponse {
    private String message;

}