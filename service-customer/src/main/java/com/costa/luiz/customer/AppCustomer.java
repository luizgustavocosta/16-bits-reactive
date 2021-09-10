package com.costa.luiz.customer;

import io.r2dbc.spi.ConnectionFactory;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Repository
interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
}

@SpringBootApplication
public class AppCustomer {

    public static void main(String[] args) {
        SpringApplication.run(AppCustomer.class, args);
    }

    @Bean
        //Explicit transaction demarcation
    TransactionalOperator transactionalOperator(ReactiveTransactionManager reactiveTransactionManager) {
        return TransactionalOperator.create(reactiveTransactionManager);
    }

    @Bean
        //Txn Management
    ReactiveTransactionManager r2dbTransactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }
}

@RestController
@RequestMapping("/api/v1")
class CustomerController {
    private final CustomerRepository repository;

    CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    @GetMapping(path = "/customers", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Customer> findAll() {
        return repository.findAll();
    }

    @GetMapping(path = "/customers/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Customer> findAllStream() {
        Stream<Customer> customerStream = Stream.of(repository.findAll()).flatMap(this::map);
        return Flux.fromStream(customerStream);
    }

    private Stream<Customer> map(Flux<Customer> customerFlux) {
        return Stream.of(null);
    }

    // Function reactive style endpoint
    @Bean
    RouterFunction<ServerResponse> routes(CustomerRepository repository) {
        AtomicLong counter = new AtomicLong();
        return route()
                .GET("/customers/stream", serverRequest ->
                        ok().contentType(MediaType.TEXT_EVENT_STREAM)
//                                .body(Flux.fromStream(Stream.generate(counter::incrementAndGet))
//                                        .delayElements(Duration.ofSeconds(1)), String.class))
                                .body(repository.findAll(), Customer.class))
                .GET("/customers", serverRequest -> ok().body(repository.findAll(), List.class))
                .GET("/customers/{id}", serverRequest -> ok()
                        .body(repository.findById(Long.parseLong(serverRequest.pathVariable("id"))), Customer.class))
                .build();
    }
}

@Service
@RequiredArgsConstructor
class CustomerService {

    private final CustomerRepository repository;
    private final TransactionalOperator transactionalOperator;

    // Change to DTO
    Flux<Customer> saveAll(List<CustomerDTO> customersDTO) {
        Flux<Customer> customers = Flux.fromStream(customersDTO.stream())
                .map(customerDTO -> {
                    return new Customer(customerDTO.getId(), customerDTO.getName(), customerDTO.getMiddleName(),
                            customerDTO.getLastName(), customerDTO.getBecameCustomer());
                })
                .flatMap(repository::save)
                .doOnNext(this::isValid);
        return transactionalOperator.transactional(customers);
        // or
        //@EnableTransactionManagement + method public
        //return customers;
    }


    private void isValid(Customer customer) {
        Assert.isTrue(nonNull(customer.getName()) &&
                        customer.getName().length() > 0 &&
                        Character.isUpperCase(customer.getName().charAt(0)),
                "The name must start with a capital letter");
    }
}

@Slf4j
@Component
class DataInitializer {

    private final CustomerRepository repository;

    DataInitializer(CustomerRepository repository) {
        this.repository = repository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ready() {

        List<Customer> listOfMovies = List.of(
                new Customer(null, "John", "", "Chad Stahelski", LocalDate.of(2020, Month.JANUARY, 2)),
                new Customer(null, "Juan", "", "Chad Stahelski", LocalDate.of(2020, Month.MARCH, 20)),
                new Customer(null, "Joan", "", "Chad Stahelski", LocalDate.of(2020, Month.APRIL, 19)),
                new Customer(null, "Joao", "", "Chad Stahelski", LocalDate.of(2021, Month.JULY, 10)));

        Flux<Customer> movies = Flux.fromIterable(listOfMovies).flatMap(repository::save);
        movies.subscribe(movie -> log.info("movie ->" + movie)); // For in-memory database

        // For PostgreSQL
//        repository
//                .deleteAll()
//                .thenMany(movies)
//                .thenMany(this.repository.findAll())
//                .subscribe(movie -> log.info("--> {}", movie));
    }
}


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "customers")
class Customer {

    @Id
    private Long id;
    private String name;
    @Column("middlename")
    private String middleName;
    @Column("lastname")
    private String lastName;
    @Column("becamecustomer")
    private LocalDate becameCustomer;
}

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
class CustomerDTO {
    private Long id;
    private String name;
    private String middleName;
    private String lastName;
    private LocalDate becameCustomer;
}

@Component
// http://localhost:8082/ jdbc:h2:mem:customers admin admin
class H2 {

    private org.h2.tools.Server webServer;

    private org.h2.tools.Server tcpServer;

    @EventListener(org.springframework.context.event.ContextRefreshedEvent.class)
    public void start() throws java.sql.SQLException {
        this.webServer = org.h2.tools.Server.createWebServer("-webPort", "8082", "-tcpAllowOthers").start();
        this.tcpServer = org.h2.tools.Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();
    }

    @EventListener(org.springframework.context.event.ContextClosedEvent.class)
    public void stop() {
        this.tcpServer.stop();
        this.webServer.stop();
    }

}