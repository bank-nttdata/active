package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Active;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Interface Service
public interface StaffService {
    Mono<Active> saveStaff(Active dataStaff);
    Mono<Active> updateStaff(Active dataStaff);
    Flux<Active> findAllStaff();
    Mono<Active> findByAccountNumberStaff(String accountNumber);
    Flux<Active> findByCustomerStaff(String dni);
    Mono<Void> deleteStaff(String accountNumber);
}
