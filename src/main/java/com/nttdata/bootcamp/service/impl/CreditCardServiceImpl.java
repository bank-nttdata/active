//package com.nttdata.bootcamp.service.impl;
//
//import com.nttdata.bootcamp.entity.Active;
//import com.nttdata.bootcamp.repository.ActiveRepository;
//import com.nttdata.bootcamp.service.CreditCardService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//@Service
//public class CreditCardServiceImpl implements CreditCardService {
//
//    @Autowired
//    private ActiveRepository activeRepository;
//
//
//    @Override
//    public Mono<Active> saveCreditCard(Active dataActiveCreditCard){
//        dataActiveCreditCard.setBusiness(false);
//        dataActiveCreditCard.setStaff(false);
//        dataActiveCreditCard.setCreditCard(true);
//        Mono<Active> activeMono = findByAccountNumberCreditCard(dataActiveCreditCard.getAccountNumber())
//                .flatMap(__ -> Mono.<Active>error(new Error("The credit card " + dataActiveCreditCard.getAccountNumber() + " exist")))
//                .switchIfEmpty(activeRepository.save(dataActiveCreditCard));
//        return activeMono;
//    }
//
//    @Override
//    public Mono<Active> updateCreditCard(Active dataActiveCreditCard) {
//        Mono<Active> activeMono = findByAccountNumberCreditCard(dataActiveCreditCard.getAccountNumber());
//        //.delayElement(Duration.ofMillis(1000));
//        try {
//            dataActiveCreditCard.setDni(activeMono.block().getDni());
//            dataActiveCreditCard.setCreationDate(activeMono.block().getCreationDate());
//            return activeRepository.save(dataActiveCreditCard);
//        }catch (Exception e){
//            return Mono.<Active>error(new Error("The credit card " + dataActiveCreditCard.getAccountNumber() + " does not exists"));
//        }
//    }
//
//
//    @Override
//    public Flux<Active> findAllCreditCard() {
//        Flux<Active> actives = activeRepository.findAll();
//        return actives;
//    }
//
//    @Override
//    public Flux<Active> findByCustomerCreditCard(String dni) {
//        Flux<Active> actives = activeRepository
//                .findAll()
//                .filter(x -> x.getDni().equals(dni));
//        return actives;
//    }
//
//    @Override
//    public Mono<Active> findByAccountNumberCreditCard(String accountNumber) {
//        Mono<Active> active = activeRepository
//                .findAll()
//                .filter(x -> x.getAccountNumber().equals(accountNumber))
//                .next();
//        return active;
//    }
//
//    @Override
//    public Mono<Void> deleteCreditCard(String accountNumber) {
//        Mono<Active> activeMono = findByAccountNumberCreditCard(accountNumber);
//        try{
//            return activeRepository.delete(activeMono.block());
//        }catch (Exception e){
//            return Mono.<Void>error(new Error("The credit card " + accountNumber + " does not exists"));
//        }
//    }
//
//}

package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Active;
import com.nttdata.bootcamp.entity.response.MessageResponse;
import com.nttdata.bootcamp.exception.NotFoundException;
import com.nttdata.bootcamp.repository.ActiveRepository;
import com.nttdata.bootcamp.service.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class CreditCardServiceImpl implements CreditCardService {

    @Autowired
    private ActiveRepository activeRepository;


    // ============================================================
    // CREATE CREDIT CARD
    // ============================================================
    @Override
    public Mono<Active> saveCreditCard(Active credit) {

        credit.setBusiness(false);
        credit.setStaff(false);
        credit.setCreditCard(true);
        Mono<String> typeCustomerMono =
                activeRepository.findByDni(credit.getDni())
                        .filter(a -> !a.getCreditCard()) // SOLO cliente base
                        .next()
                        .map(Active::getTypeCustomer)
                        .switchIfEmpty(Mono.error(
                                new RuntimeException("No existe cliente registrado con ese Dni")
                        ));

        return typeCustomerMono.flatMap(typeCustomer -> {

            credit.setTypeCustomer(typeCustomer);
            Mono<Boolean> existsCreditByAccount =
                    activeRepository
                            .findByAccountNumberAndCreditCard(credit.getAccountNumber(), true)
                            .hasElement();

            Mono<Boolean> existsCreditForPersonal =
                    "PERSONAL".equalsIgnoreCase(typeCustomer)
                            ? activeRepository
                            .findByDniAndCreditCard(credit.getDni(), true)
                            .hasElements()
                            : Mono.just(false);

            return Mono.zip(existsCreditByAccount, existsCreditForPersonal)
                    .flatMap(tuple -> {

                        if (tuple.getT1()) {
                            return Mono.error(new RuntimeException(
                                    "La tarjeta de crédito " + credit.getAccountNumber() + " ya existe"
                            ));
                        }

                        if (tuple.getT2()) {
                            return Mono.error(new RuntimeException(
                                    "El cliente PERSONAL con DNI " + credit.getDni()
                                            + " ya tiene un crédito registrado"
                            ));
                        }

                        return activeRepository.save(credit);
                    });
        });
    }





    // ============================================================
    // UPDATE CREDIT CARD
    // ============================================================
    @Override
    public Mono<Active> updateCreditCard(Active data) {

        return findByAccountNumberCreditCard(data.getAccountNumber())
                .flatMap(existing -> {

                    // SOLO se actualizan campos permitidos
                    existing.setCreditLimit(data.getCreditLimit());
                    existing.setStatus(data.getStatus());

                    // Internos del sistema
                    existing.setModificationDate(new Date());

                    return activeRepository.save(existing);
                })
                .switchIfEmpty(Mono.error(
                        new NotFoundException("The credit card " +
                                data.getAccountNumber() + " does not exist")
                ));
    }




    // ============================================================
    // FIND ALL
    // ============================================================
    @Override
    public Flux<Active> findAllCreditCard() {
        return activeRepository.findAll()
                .filter(active -> Boolean.TRUE.equals(active.getCreditCard()));
    }



    // ============================================================
    // FIND BY CUSTOMER DNI
    // ============================================================
    @Override
    public Flux<Active> findByCustomerCreditCard(String dni) {

        return activeRepository.findAll()
                .filter(active ->
                        Boolean.TRUE.equals(active.getCreditCard()) &&
                                dni.equals(active.getDni())
                );
    }


    // ============================================================
    // FIND BY ACCOUNT NUMBER
    // ============================================================
    @Override
    public Mono<Active> findByAccountNumberCreditCard(String accountNumber) {
        return activeRepository.findAll()
                .filter(active -> Boolean.TRUE.equals(active.getCreditCard()))
                .filter(active -> active.getAccountNumber().equals(accountNumber))
                .next();
    }



    // ============================================================
    // DELETE CREDIT CARD
    // ============================================================
    @Override
    public Mono<MessageResponse> deleteCreditCard(String accountNumber) {

        return findByAccountNumberCreditCard(accountNumber)
                .flatMap(active -> activeRepository.delete(active)
                        .thenReturn(new MessageResponse(
                                "Credit card account " + accountNumber + " deleted successfully")
                        )
                )
                .switchIfEmpty(Mono.error(
                        new NotFoundException("The credit card " + accountNumber + " does not exist")
                ));
    }

}

