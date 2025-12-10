package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Active;
import com.nttdata.bootcamp.entity.response.MessageResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BusinessService {
    Mono<Active> saveBusiness(Active dataBusiness);
    Mono<Active> updateBusiness(Active dataBusiness);
    Flux<Active> findAllBusiness();
    Mono<Active> findByAccountNumberBusiness(String accountNumber);
    Flux<Active> findByCustomerBusiness(String dni);
    Mono<MessageResponse> deleteBusiness(String accountNumber);
}
