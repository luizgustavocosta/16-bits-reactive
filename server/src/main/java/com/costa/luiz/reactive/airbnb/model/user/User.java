package com.costa.luiz.reactive.airbnb.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
}
