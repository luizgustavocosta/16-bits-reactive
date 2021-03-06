package com.costa.luiz.test.client.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class HotelClient {

    private final WebClient webClient;

    public Flux<HotelResponse> getAllHotels() {
        return webClient
                .get()
                .uri("http://localhost:8080/hotels")
                .retrieve().bodyToFlux(HotelResponse.class);
    }
}
