package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Active;
import com.nttdata.bootcamp.entity.response.MessageResponse;
import com.nttdata.bootcamp.exception.NotFoundException;
import com.nttdata.bootcamp.repository.ActiveRepository;
import com.nttdata.bootcamp.service.BusinessService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private ActiveRepository activeRepository;

    @Override
    public Mono<Active> saveBusiness(Active dataBusiness) {

        dataBusiness.setBusiness(true);
        dataBusiness.setStaff(false);
        dataBusiness.setCreditCard(false);

        return findByAccountNumberBusiness(dataBusiness.getAccountNumber())
                .flatMap(existing ->
                        Mono.<Active>error(
                                new RuntimeException("The business account " + dataBusiness.getAccountNumber() + " exists")
                        )
                )
                .switchIfEmpty(activeRepository.save(dataBusiness));
    }

    @Override
    public Mono<Active> updateBusiness(Active dataBusiness) {

        return findByAccountNumberBusiness(dataBusiness.getAccountNumber())
                .flatMap(existing -> {
                    dataBusiness.setDni(existing.getDni());
                    dataBusiness.setCreationDate(existing.getCreationDate());
                    return activeRepository.save(dataBusiness);
                })
                .switchIfEmpty(Mono.error(
                        new RuntimeException("The business account " + dataBusiness.getAccountNumber() + " does not exist")
                ));
    }

    @Override
    public Flux<Active> findAllBusiness() {
        return activeRepository.findAll();
    }

    @Override
    public Flux<Active> findByCustomerBusiness(String dni) {
        return activeRepository.findAll()
                .filter(active -> active.getDni().equals(dni));
    }

    @Override
    public Mono<Active> findByAccountNumberBusiness(String accountNumber) {
        return activeRepository.findAll()
                .filter(active -> active.getAccountNumber().equals(accountNumber))
                .next();
    }

    @Override
    public Mono<MessageResponse> deleteBusiness(String accountNumber) {

        return findByAccountNumberBusiness(accountNumber)
                .flatMap(active -> activeRepository.delete(active)
                        .thenReturn(new MessageResponse("Business account " + accountNumber + " deleted successfully"))
                )
                .switchIfEmpty(Mono.error(
                        new NotFoundException("The business account " + accountNumber + " does not exist")
                ));
    }




}
