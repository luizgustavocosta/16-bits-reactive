package com.costa.luiz.movie;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

interface MovieRepository extends ReactiveCrudRepository<Movie, String> {

    Flux<Movie> findMovieByName(String name);

}

@SpringBootApplication
public class AppMovie {

    public static void main(String[] args) {
        SpringApplication.run(AppMovie.class, args);
    }
}

@Slf4j
@Component
class MovieService {

    private final MovieRepository repository;

    MovieService(MovieRepository repository) {
        this.repository = repository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ready() {
        List<Movie> listOfMovies = List.of(
                new Movie(null, "John Wick", "Chad Stahelski", 111),
                new Movie(null, "John Wick II", "Chad Stahelski", 122),
                new Movie(null, "John Wick III", "Chad Stahelski", 130),
                new Movie(null, "John Wick IV", "Chad Stahelski", 0));

        Flux<Movie> movies = Flux.fromIterable(listOfMovies).flatMap(repository::save);

        repository.deleteAll()
                .thenMany(movies)
                .thenMany(this.repository.findAll());
        //.subscribe(movie -> log.info("--> {}", movie));
    }
}

@RestController
@RequestMapping("/api/v1")
@Slf4j
class MovieController {

    final MovieRepository repository;

    MovieController(MovieRepository repository) {
        this.repository = repository;
    }

    @GetMapping(path = "/movies", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Movie> findAll() {
        return repository.findAll();
    }

    @GetMapping(path = "/movies/search", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Movie> findByName(@RequestParam("name") String name) {
        log.info("Let's try find by name {}", name);
        return Flux.from(repository.findMovieByName(name));
    }
}

@Document(collection = "movies")
@AllArgsConstructor
@NoArgsConstructor
@Getter
class Movie {

    @Id
    private String id;
    private String name;
    private String director;
    private int duration;

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", director='" + director + '\'' +
                ", duration=" + duration +
                '}';
    }
}