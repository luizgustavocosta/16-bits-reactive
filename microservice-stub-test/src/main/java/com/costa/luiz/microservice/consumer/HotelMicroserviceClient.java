package com.costa.luiz.microservice.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class HotelMicroserviceClient {

    public Flux<HotelResponse> getAllHotels() {
        return WebClient.builder().build()
                .get()
                .uri("http://localhost:8080/hotels")
                .retrieve().bodyToFlux(HotelResponse.class);
    }
}
