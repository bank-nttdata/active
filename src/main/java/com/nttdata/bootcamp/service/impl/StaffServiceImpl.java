package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Active;
import com.nttdata.bootcamp.repository.ActiveRepository;
import com.nttdata.bootcamp.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Service implementation
@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    private ActiveRepository activeRepository;

    // --- SAVE ---
    @Override
    public Mono<Active> saveStaff(Active dataStaff) {

        dataStaff.setBusiness(false);
        dataStaff.setStaff(true);
        dataStaff.setCreditCard(false);

        return activeRepository.findByAccountNumber(dataStaff.getAccountNumber())
                .filter(Active::getStaff)
                .flatMap(existing ->
                        Mono.<Active>error(new RuntimeException(
                                "The staff account " + dataStaff.getAccountNumber() + " already exists"))
                )
                .switchIfEmpty(activeRepository.save(dataStaff));
    }



    // --- UPDATE ---
    @Override
    public Mono<Active> updateStaff(Active dataStaff) {

        return activeRepository.findByAccountNumber(dataStaff.getAccountNumber())
                .switchIfEmpty(Mono.error(new RuntimeException(
                        "The staff account " + dataStaff.getAccountNumber() + " does not exist")))
                .flatMap(existing -> {

                    // Mantener campos de creación
                    dataStaff.setDni(existing.getDni());
                    dataStaff.setCreationDate(existing.getCreationDate());

                    return activeRepository.save(dataStaff);
                });
    }

    // --- FIND ALL ---
    @Override
    public Flux<Active> findAllStaff() {
        return activeRepository.findAll()
                .filter(Active::getStaff);
    }

    // --- FIND BY DNI ---
    @Override
    public Flux<Active> findByCustomerStaff(String dni) {
        return activeRepository.findByDni(dni)
                .filter(Active::getStaff);
    }

    // --- FIND BY ACCOUNT NUMBER ---
    @Override
    public Mono<Active> findByAccountNumberStaff(String accountNumber) {
        return activeRepository.findByAccountNumber(accountNumber)
                .filter(Active::getStaff);
    }

    // --- DELETE ---
    @Override
    public Mono<Void> deleteStaff(String accountNumber) {
        return activeRepository.findByAccountNumber(accountNumber)
                .filter(Active::getStaff)
                .switchIfEmpty(Mono.error(new RuntimeException(
                        "The staff account " + accountNumber + " does not exist")))
                .flatMap(activeRepository::delete);
    }
}
