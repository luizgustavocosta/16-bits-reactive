package com.costa.luiz.sandbox.model;


import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Objects;
import java.util.stream.Stream;

class SubscriberTest {

    @Test
    void usingJava() {

        Flux.fromStream(getDataAsStream().filter(Objects::nonNull))
                .map(this::firstHandler)
                .doOnNext(value -> System.out.println(value))
                .map(this::secondHandler)
                .onErrorContinue(((throwable, o) -> System.out.println("An problem occurs .:"+o)))
                .doOnNext(length -> {
                    System.out.println("the length is "+length);
                })
                .subscribe();;

    }


    private int secondHandler(String s) {
        return s.length();
    }

    private String firstHandler(String s) {
        return s + "firstHandler";
    }

    private Stream<String> getDataAsStream() {
        return Stream.of("A","B", null, "C", "D");
    }


}