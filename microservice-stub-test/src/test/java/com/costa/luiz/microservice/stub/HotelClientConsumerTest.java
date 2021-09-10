package com.costa.luiz.microservice.stub;

import com.costa.luiz.microservice.consumer.HotelMicroserviceClient;
import com.costa.luiz.microservice.consumer.HotelResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static java.util.Objects.nonNull;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureStubRunner(ids = "com.costa.luiz:reactive-test:+:8080", //+ = latest definition
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
@ContextConfiguration(classes = {HotelMicroserviceClient.class})
@Disabled
class HotelClientConsumerTest {

    @Autowired
    HotelMicroserviceClient client;

    @Test
    void consumeByStub() {

        Flux<HotelResponse> response = client.getAllHotels();

        StepVerifier.create(response)
                .expectNextMatches(hotelResponse ->
                        nonNull(hotelResponse) && Character.isDigit(hotelResponse.getId().charAt(0)))
                .verifyComplete();
    }
}
