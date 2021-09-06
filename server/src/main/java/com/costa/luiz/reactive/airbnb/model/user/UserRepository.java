package com.costa.luiz.reactive.airbnb.model.user;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Flux<User> findAllUsers();

    Mono<User> findOneUserBy(String id);

    Flux<User> findByName(String name);
}
