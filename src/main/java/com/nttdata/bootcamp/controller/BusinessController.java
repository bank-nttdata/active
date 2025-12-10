package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Active;
import com.nttdata.bootcamp.entity.dto.BusinessAccountDto;
import com.nttdata.bootcamp.entity.response.MessageResponse;
import com.nttdata.bootcamp.service.BusinessService;
import com.nttdata.bootcamp.util.Constant;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Date;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/business")
public class BusinessController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessController.class);

    @Autowired
    private BusinessService businessService;

    // -----------------------------
    // Find all business accounts
    // -----------------------------
    @CircuitBreaker(name = "active", fallbackMethod = "fallbackFlux")
    @GetMapping("/findAllBusiness")
    public Flux<Active> findAllBusiness() {
        return businessService.findAllBusiness()
                .doOnNext(a -> LOGGER.info("Active business: {}", a));
    }

    // -----------------------------
    // Save Business
    // -----------------------------
    @CircuitBreaker(name = "active", fallbackMethod = "fallbackMono")
    @PostMapping("/saveBusiness")
    public Mono<Active> saveBusiness(@RequestBody BusinessAccountDto dto) {

        return Mono.just(dto)
                .map(data -> {
                    Active active = new Active();
                    active.setDni(data.getDni());
                    active.setTypeCustomer(Constant.BUSINESS_CUSTOMER);
                    active.setAccountNumber(data.getAccountNumber());
                    active.setCreditLimit(data.getCreditLimit());
                    active.setCreationDate(new Date());
                    active.setModificationDate(new Date());
                    active.setStatus(Constant.ACTIVE_ACTIVE);
                    return active;
                })
                .flatMap(businessService::saveBusiness)
                .doOnSuccess(a -> LOGGER.info("Saved business: {}", a));
    }

    // -----------------------------
    // Update Business
    // -----------------------------
    @CircuitBreaker(name = "active", fallbackMethod = "fallbackMono")
    @PutMapping("/updateBusiness/{accountNumber}")
    public Mono<Active> updateBusiness(@PathVariable String accountNumber,
                                       @Valid @RequestBody Active dataBusiness) {

        return Mono.just(dataBusiness)
                .map(active -> {
                    active.setAccountNumber(accountNumber);
                    active.setModificationDate(new Date());
                    return active;
                })
                .flatMap(businessService::updateBusiness)
                .doOnSuccess(a -> LOGGER.info("Updated business: {}", a));
    }

    // -----------------------------
    // Find by customer DNI
    // -----------------------------
    @GetMapping("/findByCustomerBusiness/{dni}")
    public Flux<Active> findByCustomerBusiness(@PathVariable String dni) {
        return businessService.findByCustomerBusiness(dni)
                .doOnNext(a -> LOGGER.info("Business for dni {} -> {}", dni, a));
    }

    // -----------------------------
    // Find by account number
    // -----------------------------
    @CircuitBreaker(name = "active", fallbackMethod = "fallbackMono")
    @GetMapping("/findByAccountNumberBusiness/{accountNumber}")
    public Mono<Active> findByAccountNumberBusiness(@PathVariable String accountNumber) {
        return businessService.findByAccountNumberBusiness(accountNumber)
                .doOnSuccess(a -> LOGGER.info("Found business for account {}", accountNumber));
    }

    // -----------------------------
    // Delete business
    // -----------------------------
    @CircuitBreaker(name = "active", fallbackMethod = "fallbackVoid")
    @DeleteMapping("/deleteBusiness/{accountNumber}")
    public Mono<MessageResponse> deleteBusiness(@PathVariable String accountNumber) {
        return businessService.deleteBusiness(accountNumber)
                .doOnSuccess(v -> LOGGER.info("Deleted business {}", accountNumber));
    }

    // -----------------------------
    // Fallbacks
    // -----------------------------
    private Mono<Active> fallbackMono(Throwable e) {
        LOGGER.error("Fallback executed: {}", e.getMessage());
        return Mono.just(new Active());
    }

    private Flux<Active> fallbackFlux(Throwable e) {
        LOGGER.error("Fallback executed: {}", e.getMessage());
        return Flux.empty();
    }

    private Mono<Void> fallbackVoid(Throwable e) {
        LOGGER.error("Fallback executed: {}", e.getMessage());
        return Mono.empty();
    }

}
