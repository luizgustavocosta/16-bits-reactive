package com.costa.luiz.reactive.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.PrincipalNameKeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;

//@SpringBootApplication
public class AppGatewayClient {
//    public static void main(String[] args) {
//        SpringApplication.run(AppGatewayClient.class, args);
//    }

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
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.pathMatchers("/proxy")
                        .authenticated())
                .build();
    }

    @Bean
    RouteLocator gateway(RouteLocatorBuilder builder) {
        // curl -v -H"Host: 16bits" http://localhost:9999/proxy
        // curl -vu luiz:costa -H"Host: 16bits" http://localhost:9999/proxy
        // To achieve Too many requests
        // while true; do curl -vu luiz:costa -H"Host: 16bits" http://localhost:9999/proxy; done
        return builder.routes()
                .route(predicateSpec -> predicateSpec
                                .host("16bits").and().path("/proxy")
                                .filters(filterSpec -> filterSpec.setPath("/reservations")
                                                .addResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                                                .requestRateLimiter(rateLimiter ->
                                                                rateLimiter.setRateLimiter(redisRateLimiter())
                                                                        .setKeyResolver(new PrincipalNameKeyResolver())
//                                                .setKeyResolver(exchange ->
//                                                        exchange.getPrincipal()
//                                                                .map(Principal::getName)
//                                                                .switchIfEmpty(Mono.empty()))
                                                )
                                ).uri("http://localhost:8080/reservations")
                )
                .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(2, 7);
    }
}
