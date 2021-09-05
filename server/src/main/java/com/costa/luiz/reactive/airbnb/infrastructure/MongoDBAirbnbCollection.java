package com.costa.luiz.reactive.airbnb.infrastructure;

import com.costa.luiz.reactive.airbnb.infrastructure.review.ReactiveReviewRepository;
import com.costa.luiz.reactive.airbnb.infrastructure.user.ReactiveUserRepository;
import com.costa.luiz.reactive.airbnb.model.review.ReviewRepository;
import com.costa.luiz.reactive.airbnb.model.review.Review;
import com.costa.luiz.reactive.airbnb.model.user.User;
import com.costa.luiz.reactive.airbnb.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MongoDBAirbnbCollection implements ReviewRepository, UserRepository {

    private final ReactiveReviewRepository reviewRepository;
    private final ReactiveUserRepository userRepository;

    public MongoDBAirbnbCollection(@Autowired ReactiveReviewRepository reviewRepository,
                                   @Autowired ReactiveUserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Flux<Review> findAll() {
        return reviewRepository.findAll();
    }

    @Override
    public Flux<Review> findAllByIdNotNull(Pageable page) {
        return reviewRepository.findAllByIdNotNull(page);
    }


    @Override
    public Mono<Review> findOne(String id) {
        return reviewRepository.findOneById(id);
    }

    @Override
    public Flux<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Mono<User> findOneUserBy(String id) {
        return userRepository.findOneById(id);
    }

    @Override
    public Flux<User> findByName(String name) {
        return userRepository.findByFirstNameContains(name);
    }
}
