package com.costa.luiz.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Stream;

@SpringBootApplication
public class AppGreeting {

    public static void main(String[] args) {
        SpringApplication.run(AppGreeting.class, args);
    }
}

@Configuration
@Slf4j
class GreetingWebSocketConfiguration {

    @Bean
    SimpleUrlHandlerMapping simpleUrlHandlerMapping(WebSocketHandler webSocketHandler) {
        return new SimpleUrlHandlerMapping(Map.of("/ws/greetings", webSocketHandler), 10);
    }

    @Bean
    WebSocketHandler webSocketHandler(GreetingService service) {
        return session -> {
            Flux<WebSocketMessage> receive = session.receive();
            Flux<String> names = receive.map(WebSocketMessage::getPayloadAsText);//could be never ending stream
            Flux<GreetingRequest> requestFlux = names.map(GreetingRequest::new);
            Flux<GreetingResponse> greetingResponseFlux = requestFlux.flatMap(service::greet);
            Flux<String> response = greetingResponseFlux.map(GreetingResponse::getMessage); //Publisher of response
            Flux<WebSocketMessage> webSocketResponse = response.map(session::textMessage);
            webSocketResponse.doFinally(signalType -> log.info("The client close the connection " + signalType));
            return session.send(webSocketResponse);
        };
    }

    @Bean
    WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
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

@Service
class GreetingService {
    Flux<GreetingResponse> greet(GreetingRequest request) {
        return Flux.fromStream(Stream.generate(() ->
                new GreetingResponse("Hi " + request.getName() + " at " + Instant.now())))
                .delayElements(Duration.ofSeconds(1));
    }
}