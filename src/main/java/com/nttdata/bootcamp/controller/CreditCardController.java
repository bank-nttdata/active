//package com.nttdata.bootcamp.controller;
//
//import com.nttdata.bootcamp.entity.Active;
//import com.nttdata.bootcamp.entity.dto.CreditCardDto;
//import com.nttdata.bootcamp.service.CreditCardService;
//import com.nttdata.bootcamp.util.Constant;
//import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import javax.validation.Valid;
//import java.util.Date;
//
//@CrossOrigin(origins = "*")
//@RestController
//@RequestMapping(value = "/creditCard")
//public class CreditCardController {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(CreditCardController.class);
//    @Autowired
//    private CreditCardService creditCardService;
//
//    //Save active credit card
//    @CircuitBreaker(name = "active", fallbackMethod = "fallBackGetCreditCard")
//    @PostMapping(value = "/saveStaffCreditCard")
//    public Mono<Active> saveStaffCreditCard(@RequestBody CreditCardDto dataCreditCard){
//        Active active= new Active();
//        Mono.just(active).doOnNext(t -> {
//                    t.setDni(dataCreditCard.getDni());
//                    t.setTypeCustomer(Constant.PERSONAL_CUSTOMER);
//                    t.setAccountNumber(dataCreditCard.getAccountNumber());
//                    t.setCreditLimit(dataCreditCard.getCreditLimit());
//                    t.setCreationDate(new Date());
//                    t.setModificationDate(new Date());
//                    t.setStatus(Constant.ACTIVE_ACTIVE);
//
//                }).onErrorReturn(active).onErrorResume(e -> Mono.just(active))
//                .onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));
//
//        Mono<Active> activeMono = creditCardService.saveCreditCard(active);
//        return activeMono;
//    }
//    @CircuitBreaker(name = "active", fallbackMethod = "fallBackGetCreditCard")
//    @PostMapping(value = "/saveBusinessCreditCard")
//    public Mono<Active> saveBusinessCreditCard(@RequestBody CreditCardDto dataCreditCard){
//        Active active= new Active();
//        Mono.just(active).doOnNext(t -> {
//                    t.setDni(dataCreditCard.getDni());
//                    t.setTypeCustomer(Constant.BUSINESS_CUSTOMER);
//                    t.setAccountNumber(dataCreditCard.getAccountNumber());
//                    t.setCreditLimit(dataCreditCard.getCreditLimit());
//                    t.setCreationDate(new Date());
//                    t.setModificationDate(new Date());
//                    t.setStatus(Constant.ACTIVE_ACTIVE);
//
//                }).onErrorReturn(active).onErrorResume(e -> Mono.just(active))
//                .onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));
//
//        Mono<Active> activeMono = creditCardService.saveCreditCard(active);
//        return activeMono;
//    }
//
//    //Update active credit card
//    @CircuitBreaker(name = "active", fallbackMethod = "fallBackGetCreditCard")
//    @PutMapping("/updateCreditCard/{accountNumber}")
//    public Mono<Active> updateBusiness(@PathVariable("accountNumber") String accountNumber,
//                                       @Valid @RequestBody Active dataCreditCard) {
//        Mono.just(dataCreditCard).doOnNext(t -> {
//
//                    t.setAccountNumber(accountNumber);
//                    t.setModificationDate(new Date());
//
//                }).onErrorReturn(dataCreditCard).onErrorResume(e -> Mono.just(dataCreditCard))
//                .onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));
//
//        Mono<Active> updateActive = creditCardService.updateCreditCard(dataCreditCard);
//        return updateActive;
//    }
//
//    //search all credit card
//    @GetMapping("/findAllCreditCard")
//    public Flux<Active> findAllCreditCard() {
//        Flux<Active> actives = creditCardService.findAllCreditCard();
//        LOGGER.info("Registered Actives credit card Products: " + actives);
//        return actives;
//    }
//
//    //Actives credit card search by customer
//
//    @GetMapping("/findByCustomerCreditCard/{dni}")
//    public Flux<Active> findByCustomerCreditCard(@PathVariable("dni") String dni) {
//        Flux<Active> actives = creditCardService.findByCustomerCreditCard(dni);
//        LOGGER.info("Registered Actives credit card Products by customer of dni: "+dni +"-" + actives);
//        return actives;
//    }
//    @CircuitBreaker(name = "active", fallbackMethod = "fallBackGetCreditCard")
//    //Search for active credit card by AccountNumber
//    @GetMapping("/findByAccountNumberCreditCard/{accountNumber}")
//    public Mono<Active> findByAccountNumberCreditCard(@PathVariable("accountNumber") String accountNumber) {
//        LOGGER.info("Searching active credit card product by accountNumber: " + accountNumber);
//        return creditCardService.findByAccountNumberCreditCard(accountNumber);
//    }
//
//    //Delete active credit card
//    @CircuitBreaker(name = "active", fallbackMethod = "fallBackGetCreditCard")
//    @DeleteMapping("/deleteCreditCard/{accountNumber}")
//    public Mono<Void> deleteCreditCard(@PathVariable("accountNumber") String accountNumber) {
//        LOGGER.info("Deleting active by accountNumber: " + accountNumber);
//        Mono<Void> delete = creditCardService.deleteCreditCard(accountNumber);
//        return delete;
//    }
//
//    private Mono<Active> fallBackGetCreditCard(Exception e){
//        Active active = new Active();
//        Mono<Active> creditCardMono = Mono.just(active);
//        return creditCardMono;
//    }
//
//}

package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Active;
import com.nttdata.bootcamp.entity.dto.CreditCardDto;
import com.nttdata.bootcamp.entity.response.MessageResponse;
import com.nttdata.bootcamp.service.CreditCardService;
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

@RestController
@RequestMapping("/creditCard")
@CrossOrigin(origins = "*")
public class CreditCardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditCardController.class);

    @Autowired
    private CreditCardService creditCardService;


    // ============================================================
    // SAVE CREDIT CARD (PERSONAL O BUSINESS)
    // ============================================================
    @CircuitBreaker(name = "active", fallbackMethod = "fallbackCreditCard")
    @PostMapping("/save")
    public Mono<Active> saveCreditCard(@RequestBody @Valid CreditCardDto dto) {

        Active active = new Active();
        active.setRuc(dto.getRuc());
        active.setDni(dto.getDni());
        active.setAccountNumber(dto.getAccountNumber());
        active.setCreditLimit(dto.getCreditLimit());
        active.setCreationDate(new Date());
        active.setModificationDate(new Date());
        active.setStatus(Constant.ACTIVE_ACTIVE);
        active.setStaff(false);
        active.setBusiness(false);
        active.setCreditCard(true);

        LOGGER.info("Saving credit card: {}", dto);

        return creditCardService.saveCreditCard(active);
    }


    // ============================================================
    // UPDATE CREDIT CARD
    // ============================================================
    @CircuitBreaker(name = "active", fallbackMethod = "fallbackCreditCard")
    @PutMapping("/update/{accountNumber}")
    public Mono<Active> updateCreditCard(
            @PathVariable String accountNumber,
            @Valid @RequestBody Active data) {

        data.setAccountNumber(accountNumber);
        data.setModificationDate(new Date());

        LOGGER.info("Updating credit card {}", accountNumber);

        return creditCardService.updateCreditCard(data);
    }


    // ============================================================
    // GET ALL CREDIT CARDS
    // ============================================================
    @GetMapping("/findAll")
    public Flux<Active> findAll() {
        LOGGER.info("Listing all credit cards");
        return creditCardService.findAllCreditCard();
    }


    // ============================================================
    // FIND BY DNI
    // ============================================================
    @GetMapping("/findByCustomer/{dni}")
    public Flux<Active> findByCustomer(@PathVariable String dni) {
        LOGGER.info("Searching credit cards by dni {}", dni);
        return creditCardService.findByCustomerCreditCard(dni);
    }


    // ============================================================
    // FIND BY ACCOUNT NUMBER
    // ============================================================
    //@CircuitBreaker(name = "active", fallbackMethod = "fallbackCreditCard")
    @GetMapping("/findByAccount/{accountNumber}")
    public Mono<Active> findByAccount(@PathVariable String accountNumber) {
        LOGGER.info("Searching credit card {}", accountNumber);
        return creditCardService.findByAccountNumberCreditCard(accountNumber);
    }


    // ============================================================
    // DELETE CREDIT CARD
    // ============================================================
    @CircuitBreaker(name = "active", fallbackMethod = "fallbackDeleteCreditCard")
    @DeleteMapping("/delete/{accountNumber}")
    public Mono<MessageResponse> delete(@PathVariable String accountNumber) {

        LOGGER.info("Deleting credit card {}", accountNumber);

        return creditCardService.deleteCreditCard(accountNumber);
    }


    // ============================================================
    // FALLBACKS
    // ============================================================

    private Mono<Active> fallbackCreditCard(String param, Throwable ex) {
        LOGGER.error("CircuitBreaker fallback for {} - {}", param, ex.getMessage());
        return Mono.error(new RuntimeException("Credit card service temporarily unavailable"));
    }

    private Mono<MessageResponse> fallbackDeleteCreditCard(String param, Throwable ex) {
        LOGGER.error("CircuitBreaker fallback DELETE for {} - {}", param, ex.getMessage());
        return Mono.just(new MessageResponse("Unable to delete credit card " + param + " (fallback activated)"));
    }
}

