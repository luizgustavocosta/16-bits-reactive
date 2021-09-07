package com.costa.luiz.test.producer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest
@Import(HotelHttpConfiguration.class)
class HotelControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    HotelRepository hotelRepository;

    @Test
    void getAllHotels() {
        var id = 1976;
        var hotelName = "Hotel California";
        when(hotelRepository.findAll()).thenReturn(Flux.just(new Hotel(id, hotelName)));
        webTestClient.get().uri("/hotels")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("@.[0].id").isEqualTo(id)
                .jsonPath("@.[0].name").isEqualTo(hotelName);
    }
}