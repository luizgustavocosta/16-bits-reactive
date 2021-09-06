package com.costa.luiz.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Repository
interface ReservationRepository extends ReactiveCrudRepository<Reservation, Integer> {
}

@SpringBootApplication
public class AppGateway {

    public static void main(String[] args) {
        SpringApplication.run(AppGateway.class, args);
    }

    @Bean
    RouterFunction<ServerResponse> routesGateway(ReservationRepository repository) {
        return route()
                .GET("/reservations", serverRequest ->
                        ServerResponse.ok().body(repository.findAll(), Reservation.class))
                .build();
    }
}

@Component
@RequiredArgsConstructor
class DataGatewayInitializer {

    private final ReservationRepository repository;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        Flux<Reservation> reservations = Flux.just(
                new Reservation(null, "Ibis"),
                new Reservation(null, "Melia"),
                new Reservation(null, "Novotel"),
                new Reservation(null, "Hilton"));
        this.repository.saveAll(reservations).subscribe();
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "reservations")
class Reservation {
    @Id
    private Integer id;
    private String name;
}
