package com.costa.luiz.test;

import com.costa.luiz.test.producer.Hotel;
import com.costa.luiz.test.producer.HotelRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;

@SpringBootTest(properties = "server.port=0")
@ExtendWith(SpringExtension.class)
public class BaseClass {

    @MockBean
    HotelRepository repository;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        Mockito.when(repository.findAll())
                .thenReturn(Flux.just(new Hotel(99, "Ibiza Hotel")));

    }
}
