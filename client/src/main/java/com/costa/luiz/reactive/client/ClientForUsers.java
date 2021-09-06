package com.costa.luiz.reactive.client;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class ClientForUsers {

    public static void main(String[] args) {
        new ClientForUsers().getAllUsers();
    }

    private void getAllUsers() {
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory("http://localhost:8082");
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        WebClient client = WebClient.builder().uriBuilderFactory(uriBuilderFactory)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_STREAM_JSON_VALUE) //NDJSON is a convenient format for storing or streaming structured data that may be processed one record at a time.
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create(ConnectionProvider.newConnection())))
                .build();
        AtomicLong count = new AtomicLong();
        Flux.from(client.get().uri("/api/v1/users")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .retrieve()
                .bodyToFlux(User.class))
                .doOnNext(row -> {
                    count.incrementAndGet();
                    log.info(row.toString());
                })
                .doOnComplete(() -> log.info("Received {} rows", count.get()))
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .doOnNext(row -> log.info("ROW {}", row))
                .blockLast();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class User {
        private String id;
        private String firstName;
        private String lastName;
        private String email;
    }
}
