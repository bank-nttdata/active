package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Active;
import com.nttdata.bootcamp.entity.dto.StaffAccountDto;
import com.nttdata.bootcamp.util.Constant;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nttdata.bootcamp.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Date;
import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/staff")
public class StaffController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaffController.class);

    @Autowired
    private StaffService staffService;

    // --- SAVE ---
    @CircuitBreaker(name = "active", fallbackMethod = "fallBackGetStaff")
    @PostMapping("/saveStaff")
    public Mono<Active> saveStaff(@RequestBody @Valid StaffAccountDto dataStaff) {

        Active active = new Active();

        return Mono.fromSupplier(() -> {
                    active.setDni(dataStaff.getDni());
                    active.setTypeCustomer(Constant.PERSONAL_CUSTOMER);
                    active.setAccountNumber(dataStaff.getAccountNumber());
                    active.setCreditLimit(dataStaff.getCreditLimit());
                    active.setCreationDate(new Date());
                    active.setModificationDate(new Date());
                    active.setStatus(Constant.ACTIVE_ACTIVE);
                    return active;
                })
                .flatMap(staffService::saveStaff)
                .doOnSuccess(a -> LOGGER.info("Saved Staff: {}", a))
                .doOnError(e -> LOGGER.error("Error saving staff", e));
    }

    // --- UPDATE ---
    @CircuitBreaker(name = "active", fallbackMethod = "fallBackGetStaff")
    @PutMapping("/updateStaff/{accountNumber}")
    public Mono<Active> updateStaff(@PathVariable("accountNumber") String accountNumber,
                                    @Valid @RequestBody StaffAccountDto dto) {

        return staffService.findByAccountNumberStaff(accountNumber)
                .switchIfEmpty(Mono.error(new RuntimeException("Not found")))
                .flatMap(existing -> {

                    existing.setDni(dto.getDni());                    // Sí actualiza DNI
                    existing.setCreditLimit(dto.getCreditLimit());    // Si deseas
                    existing.setModificationDate(new Date());         // Fecha update

                    return staffService.updateStaff(existing);
                });
    }


    // --- FIND ALL ---
    @GetMapping("/findAllStaff")
    public Flux<Active> findAllStaff() {
        return staffService.findAllStaff()
                .doOnSubscribe(s -> LOGGER.info("Searching all staff accounts"));
    }

    // --- FIND BY CUSTOMER ---
    @GetMapping("/findByCustomerStaff/{dni}")
    public Flux<Active> findByCustomerStaff(@PathVariable("dni") String dni) {
        return staffService.findByCustomerStaff(dni)
                .doOnSubscribe(s -> LOGGER.info("Searching staff by customer {}", dni));
    }

    // --- FIND BY ACCOUNT NUMBER ---
    @CircuitBreaker(name = "active", fallbackMethod = "fallBackGetStaff")
    @GetMapping("/findByAccountNumberStaff/{accountNumber}")
    public Mono<Active> findByAccountNumberStaff(@PathVariable("accountNumber") String accountNumber) {
        return staffService.findByAccountNumberStaff(accountNumber)
                .doOnSubscribe(s -> LOGGER.info("Searching staff by accountNumber {}", accountNumber));
    }

    // --- DELETE ---
    @CircuitBreaker(name = "active", fallbackMethod = "fallBackGetStaffVoid")
    @DeleteMapping("/deleteStaff/{accountNumber}")
    public Mono<Void> deleteStaff(@PathVariable("accountNumber") String accountNumber) {
        return staffService.deleteStaff(accountNumber)
                .doOnSubscribe(s -> LOGGER.info("Deleting staff account {}", accountNumber));
    }

    // --- FALLBACKS ---
    private Mono<Active> fallBackGetStaff(Exception e) {
        LOGGER.error("Fallback executed due to error: {}", e.getMessage());
        return Mono.just(new Active());
    }

    private Mono<Void> fallBackGetStaffVoid(Exception e) {
        LOGGER.error("Fallback executed (void) due to error: {}", e.getMessage());
        return Mono.empty();
    }
}

