package com.costa.luiz.test.producer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class HotelRepositoryTest {

    @Autowired
    HotelRepository repository;

    @Test
    void persist() {
        Flux<Hotel> hotels = repository.deleteAll()
                .thenMany(repository.saveAll(
                        Flux.just(
                                new Hotel("Hotel AC"),
                                new Hotel("Hotel DC"),
                                new Hotel("Melia"),
                                new Hotel("Ibis"))))
                .thenMany(repository.findByNameStartingWith("Hotel"));

        StepVerifier.create(hotels)
                .expectNextCount(2)
                .verifyComplete();

    }

    @Test
    void persistStepByStep() {
        Flux<Hotel> hotels = repository.saveAll(
                Flux.just(
                        new Hotel("Hotel AC"),
                        new Hotel("Hotel DC"),
                        new Hotel("Melia"),
                        new Hotel("Ibis")));

        StepVerifier.create(repository.deleteAll()).verifyComplete();
        StepVerifier.create(hotels).expectNextCount(4).verifyComplete();
        StepVerifier.create(repository.findByNameStartingWith("Hotel")).expectNextCount(2).verifyComplete();

    }
}
