package com.costa.luiz.reactive.airbnb.infrastructure.review;

import com.costa.luiz.reactive.airbnb.model.review.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ReactiveReviewRepository extends ReactiveCrudRepository<Review, String> {

    Flux<Review> findAllByIdNotNull(final Pageable page);

    Mono<Review> findOneById(String id);
}
