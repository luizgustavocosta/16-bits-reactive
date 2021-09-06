package com.costa.luiz.reactive.airbnb.model.review;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewRepository {

    Flux<Review> findAll();

    Flux<Review> findAllByIdNotNull(final Pageable page);

    Mono<Review> findOne(String id);
}
