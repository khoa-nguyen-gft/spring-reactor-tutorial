package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}

@Component
@AllArgsConstructor
@Log4j2
class sampleDataInitializer {

    private final ReservationRepository reservationRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void ready() {
        Flux<Reservation> reservationFlux = Flux.just("Madhude", "Josh", "Olga", "Marcin")
                .map(name -> new Reservation(null, name))
                .flatMap(r -> reservationRepository.save(r));

        reservationRepository.deleteAll()
                .thenMany(reservationFlux)
                .thenMany(reservationRepository.findAll())
                .subscribe(log::info);
    }
}

interface ReservationRepository extends ReactiveCrudRepository<Reservation, String> {

}

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
class Reservation {
    @Id
    private String id;
    private String name;
}
