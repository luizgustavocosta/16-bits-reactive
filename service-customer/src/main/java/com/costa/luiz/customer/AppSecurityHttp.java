package com.costa.luiz.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class AppSecurityHttp {

    public static void main(String[] args) {
        SpringApplication.run(AppSecurityHttp.class, args);
    }

    @Bean
    MapReactiveUserDetailsService authentication() {
        return new MapReactiveUserDetailsService(User.withDefaultPasswordEncoder()
                .username("luiz")
                .password("costa")
                .roles("USER")
                .build());
    }

    @Bean
    SecurityWebFilterChain authorization(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(Customizer.withDefaults())
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.pathMatchers("/secure/*")
                        .authenticated()
                        .anyExchange().permitAll())
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> securityRoutes(SecurityService service) {
        return route()
                .GET("/secure/greetings", serverRequest -> ServerResponse.ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(
                                serverRequest.principal()
                                        .map(Principal::getName)
                                        .map(GreetingRequest::new)
                                        .flatMapMany(service::many), GreetingResponse.class))

                .GET("/secure/greeting", serverRequest -> ServerResponse.ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(
                                serverRequest.principal()
                                        .map(Principal::getName)
                                        .map(GreetingRequest::new)
                                        .flatMap(service::once), GreetingResponse.class))
                .build();
    }
}

@Service
class SecurityService {

    Flux<GreetingResponse> many(GreetingRequest request) {
        return Flux.fromStream(Stream.generate(() -> greet(request))).delayElements(Duration.ofSeconds(1));
    }

    Mono<GreetingResponse> once(GreetingRequest request) {
        return Mono.just(greet(request));
    }

    private GreetingResponse greet(GreetingRequest request) {
        return new GreetingResponse("Hello " + request.getName() + " @ " + Instant.now());
    }

}