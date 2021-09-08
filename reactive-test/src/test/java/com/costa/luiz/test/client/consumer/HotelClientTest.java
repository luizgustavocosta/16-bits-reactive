package com.costa.luiz.test.client.consumer;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static java.util.Objects.nonNull;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureWireMock(port = 8080) // 8080 is the default port
class HotelClientTest {

    @Autowired
    HotelClient client;

    @Test
    void consume() {
        WireMock.stubFor(
                WireMock.get(WireMock.urlEqualTo("/hotels"))
                        .willReturn(WireMock.aResponse()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody("[{ \"hotelId\":\"42\", \"hotelName\":\"Blade Runner\"  }]")
                                .withStatus(HttpStatus.OK.value())));

        Flux<HotelResponse> response = client.getAllHotels();

        StepVerifier.create(response)
                .expectNextMatches(hotelResponse ->
                        nonNull(hotelResponse) && Character.isDigit(hotelResponse.getHotelId().charAt(0)))
                .verifyComplete();
    }
}
