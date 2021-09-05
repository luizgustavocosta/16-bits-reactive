package com.costa.luiz.reactive.airbnb.controller.user;

import com.costa.luiz.reactive.airbnb.model.user.User;
import com.costa.luiz.reactive.airbnb.model.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping(produces = {MediaType.TEXT_EVENT_STREAM_VALUE, MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Flux<User> findAll() {
        return repository.findAllUsers();
    }

    @GetMapping(value = "{id}")
    public Mono<User> findOne(@PathVariable String id) {
        log.info("Looking for the id {}", id);
        return repository.findOneUserBy(id);
    }

    @GetMapping
    public Flux<User> findByName(@RequestParam("name") String name) {
        log.info("Looking for the name {}", name);
        return repository.findByName(name);
    }

}
