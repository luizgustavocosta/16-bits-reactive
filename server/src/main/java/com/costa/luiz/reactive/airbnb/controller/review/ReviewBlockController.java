package com.costa.luiz.reactive.airbnb.controller.review;

import com.costa.luiz.reactive.airbnb.infrastructure.review.ReviewBlockRepository;
import com.costa.luiz.reactive.airbnb.model.review.Review;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api/v1/airbnb/block/reviews")
@RestController
public class ReviewBlockController {

    private final ReviewBlockRepository repository;

    public ReviewBlockController(ReviewBlockRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Review> findAll() {
        return repository.findAll().stream().collect(Collectors.toUnmodifiableList());
    }

    @GetMapping("{id}")
    public Review findOne(@PathVariable String id) {
        return repository.findById(id).orElseThrow(IllegalArgumentException::new);
    }
}
