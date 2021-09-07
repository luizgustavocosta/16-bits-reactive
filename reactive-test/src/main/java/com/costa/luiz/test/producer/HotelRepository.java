package com.costa.luiz.test.producer;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface HotelRepository extends ReactiveCrudRepository<Hotel, Integer> {

    Flux<Hotel> findByNameStartingWith(String name);
}
