package com.costa.luiz.reactive.airbnb.infrastructure.user;

import com.costa.luiz.reactive.airbnb.model.user.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ReactiveUserRepository extends ReactiveCrudRepository<User, String> {

    Mono<User> findOneById(String id);

    //    @Tailable
    Flux<User> findByFirstNameContains(String name);
}
