package com.costa.luiz.reactive.airbnb.controller.review;

import com.costa.luiz.reactive.airbnb.model.review.Review;
import com.costa.luiz.reactive.airbnb.model.review.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/airbnb/reviews")
@Slf4j
public class ReviewController {

    private final ReviewRepository repository;

    public ReviewController(ReviewRepository repository) {
        this.repository = repository;
    }

    @GetMapping( produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Review> findAll() {
        return repository.findAll();
    }

    @GetMapping(value = "{page}/{size}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Review> findAll(@PathVariable int page, @PathVariable int size) {
        return repository.findAllByIdNotNull(PageRequest.of(page, size)); // Programmatically
    }

    @GetMapping(value = "{id}")
    public Mono<Review> findOne(@PathVariable String id) {
        log.info("Calling by id {}", id);
        return repository.findOne(id);
    }

}
