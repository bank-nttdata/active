package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Active;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditCardService {
    Mono<Active> saveCreditCard(Active dataCreditCard);
    Mono<Active> updateCreditCard(Active dataCreditCard);
    Flux<Active> findAllCreditCard();
    Mono<Active> findByAccountNumberCreditCard(String accountNumber);
    Flux<Active> findByCustomerCreditCard(String dni);
    Mono<Void> deleteCreditCard(String accountNumber);
}
