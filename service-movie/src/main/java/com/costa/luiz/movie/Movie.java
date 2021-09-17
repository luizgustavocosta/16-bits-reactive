package com.costa.luiz.movie;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "movies")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Movie {

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