package com.nttdata.bootcamp.repository;

import com.nttdata.bootcamp.entity.Active;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ActiveRepository extends ReactiveCrudRepository<Active, String> {
    Mono<Active> findByAccountNumber(String accountNumber);

    Flux<Active> findByDni(String dni);
}
