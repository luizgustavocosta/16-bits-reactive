package com.costa.luiz.customer;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;
import akka.stream.scaladsl.Sink;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class AppReactiveStreams {
    public static void main(String[] args) {
        SpringApplication.run(AppReactiveStreams.class, args);
    }

    @Bean
    RouterFunction<ServerResponse> twitterRoutes(TweetService service) {
        return route()
                .GET("/tweets", serverRequest -> ServerResponse.ok()
                        .body(service.allTweets(), Tweet.class))
                .GET("/hashtags", serverRequest -> ServerResponse.ok()
                        .body(service.allHashTags(), HashTag.class))

                .build();
    }
}

@Configuration
@RequiredArgsConstructor
@Log4j2
class TweetInitializer {

    private final TweetRepository repository;

    @EventListener(ApplicationReadyEvent.class)
    void tweet() {
        Author justin = new Author("justin"), luke = new Author("luke_skywalker"),
                theRock = new Author("dwayne_johnson");

        Flux<Tweet> tweets = Flux.just(
                new Tweet("Music #music #mtv", justin),
                new Tweet("Let's rock! #movie #gym #therock", theRock),
                new Tweet("May the force by with you #starwars #force #movie", luke));

        repository.deleteAll()
                .thenMany(repository.saveAll(tweets))
                .thenMany(repository.findAll())
                ;//.subscribe(log::info);
    }
}

@Configuration
class AkkaConfiguration {
    @Bean
    ActorSystem actorSystem() {
        return ActorSystem.create("peter-griffin-akka-stream");
    }

    @Bean
    ActorMaterializer actorMaterializer() {
        return ActorMaterializer.create(this.actorSystem());
    }
}

@Service
@RequiredArgsConstructor
@Log4j2
class TweetService {

    private final TweetRepository repository;
    private final ActorMaterializer actorMaterializer;

    Publisher<Tweet> allTweets() {
        return repository.findAll();
    }

    Publisher<HashTag> allHashTags() {
        return Source
                .fromPublisher(allTweets())
                .map(Tweet::getHashTags)
                .reduce(this::join)
                .mapConcat(hashTags -> hashTags)
                .runWith(Sink.asPublisher(true), this.actorMaterializer);
    }

    private <T> Set<T> join(Set<T> hashTags, Set<T> otherHashTag) {
        Set<T> twitterHashTags = new HashSet<>();
        twitterHashTags.addAll(hashTags);
        twitterHashTags.addAll(otherHashTag);
        return twitterHashTags;
    }

}

interface TweetRepository extends ReactiveMongoRepository<Tweet, String> {

}

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
class Author {
    @Id
    private String handle;
}

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
class Tweet {
    private String id;
    private String text;
    private Author author;

    public Tweet(String text, Author author) {
        this.text = text;
        this.author = author;
    }

    public Set<HashTag> getHashTags() {
        return Arrays.stream(this.text.split(" "))
                .filter(text -> text.startsWith("#"))
                .map(word -> new HashTag(word.replaceAll("[ˆ#]", "").toLowerCase(Locale.ROOT)))
                .collect(Collectors.toSet());
    }
}

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
class HashTag {
    @Id
    private String id;
}