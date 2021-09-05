package com.costa.luiz.reactive.client;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@SpringBootApplication
public class AppCustomerClient {
    public static void main(String[] args) {
        SpringApplication.run(AppCustomerClient.class, args);
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8080/api/v1/").build();
    }
}

@Component
@Log4j2
@RequiredArgsConstructor
class Client {

    private final WebClient webClient;

    @EventListener(ApplicationReadyEvent.class)
    public void ready() {
        this.webClient.get()
                .uri("customers")
                .retrieve()
                .bodyToFlux(CustomerResponse.class)
                //.retry(10) //number of times or
                .onErrorMap(throwable -> new IllegalArgumentException("Provide another url"))
//                .doOnError(throwable -> new CustomerResponse(-1L, null, null, null, null)) //Affect the result
                .onErrorResume(IllegalStateException.class, exception -> Flux.just(new ErrorMessage("Handling a specific exception")))
                .onErrorResume(IllegalArgumentException.class, exception -> Flux.just(new ErrorMessage("Handling illegal argument")))
                .onErrorResume(throwable -> Flux.just(new ErrorMessage("Response received by the server [" + throwable.getMessage() + "]")))
                .subscribe(log::info);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class CustomerResponse {
    private Long id;
    private String name;
    private String middleName;
    private String lastName;
    private LocalDate becameCustomer;

}

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
class ErrorMessage extends CustomerResponse {
    private String message;
}