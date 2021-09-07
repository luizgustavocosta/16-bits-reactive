package com.costa.luiz.test.producer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static java.util.Objects.nonNull;

@DisplayName("Entity test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
class HotelEntityTest {

    @Autowired
    private R2dbcEntityTemplate template;

    @Test
    void example() {
        StepVerifier
                .create(Flux.just("1", "2", "3"))
                .expectNext("1", "2", "3")
                .verifyComplete();
    }

    @Test
    void persist() {
        Hotel hotel = new Hotel(null, "Luiz");
        Mono<Hotel> saveRecord = template.insert(hotel);
        StepVerifier
                .create(saveRecord)
                .expectNextMatches(HotelEntityTest::verifyName)
                .verifyComplete();
    }

    private static boolean verifyName(Hotel savedHotel) {
        return nonNull(savedHotel) && "Luiz".equals(savedHotel.getName());
    }
}
