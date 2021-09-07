package com.costa.luiz.test.producer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class HotelHttpConfiguration {

    @Bean
    RouterFunction<ServerResponse> hotelRoutes(HotelRepository repository) {
        return route()
                .GET("/hotels", serverRequest -> ServerResponse.ok().body(repository.findAll(), Hotel.class))
                .build();
    }
}
