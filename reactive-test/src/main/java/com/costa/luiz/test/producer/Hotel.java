package com.costa.luiz.test.producer;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor @Getter @Setter
@NoArgsConstructor
@Table(value = "hotels")
public class Hotel {

    @Id
    private Integer id;
    private String name;

    public Hotel(String name) {
        this.name = name;
    }
}
