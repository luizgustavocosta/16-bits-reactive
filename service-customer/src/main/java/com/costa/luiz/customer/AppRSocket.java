package com.costa.luiz.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

@SpringBootApplication
public class AppRSocket {

    public static void main(String[] args) {
        SpringApplication.run(AppRSocket.class, args);
        //System.in.read(); //To maintain running
    }
}

@Controller
@RequiredArgsConstructor
class GreetingsController {

    private final RSocketService service;

    @MessageMapping("greetings.{timeInSeconds}")
    Flux<GreetingResponse> greet(
            @DestinationVariable("timeInSeconds") int timeInSeconds,
            GreetingRequest request) {
        return service.greeting(request, timeInSeconds);
    }
}

@Component
@RequiredArgsConstructor
class JsonHelper {
    private final ObjectMapper mapper;

    @SneakyThrows
    <T> T read(String payload, Class<T> clazz) {
        return this.mapper.readValue(payload, clazz);
    }

    @SneakyThrows
    String write(Object object) {
        return this.mapper.writeValueAsString(object);
    }
}


@Service
class RSocketService {

    Flux<GreetingResponse> greeting(GreetingRequest request, int delay) {
        return Flux.fromStream(Stream.generate(() ->
                new GreetingResponse("Hello " + request.getName() + " at " + Instant.now())))
                .delayElements(Duration.ofSeconds(delay));
    }
}