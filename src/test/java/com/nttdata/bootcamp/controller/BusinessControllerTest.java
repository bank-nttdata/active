package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Active;
import com.nttdata.bootcamp.entity.dto.BusinessAccountDto;
import com.nttdata.bootcamp.entity.response.MessageResponse;
import com.nttdata.bootcamp.service.BusinessService;
import com.nttdata.bootcamp.util.Constant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(BusinessController.class)
class BusinessControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BusinessService businessService;

    private Active businessAccount1;
    private Active businessAccount2;
    private BusinessAccountDto businessAccountDto;
    private MessageResponse messageResponse;

    @BeforeEach
    void setUp() {
        // Setup Business Account 1
        businessAccount1 = new Active();
        businessAccount1.setId("1");
        businessAccount1.setDni("20123456789");
        businessAccount1.setTypeCustomer(Constant.BUSINESS_CUSTOMER);
        businessAccount1.setAccountNumber("BUS-001");
        businessAccount1.setCreditLimit(50000.0);
        businessAccount1.setStatus(Constant.ACTIVE_ACTIVE);
        businessAccount1.setCreationDate(new Date());
        businessAccount1.setModificationDate(new Date());

        // Setup Business Account 2
        businessAccount2 = new Active();
        businessAccount2.setId("2");
        businessAccount2.setDni("20987654321");
        businessAccount2.setTypeCustomer(Constant.BUSINESS_CUSTOMER);
        businessAccount2.setAccountNumber("BUS-002");
        businessAccount2.setCreditLimit(75000.0);
        businessAccount2.setStatus(Constant.ACTIVE_ACTIVE);
        businessAccount2.setCreationDate(new Date());
        businessAccount2.setModificationDate(new Date());

        // Setup DTO
        businessAccountDto = new BusinessAccountDto();
        businessAccountDto.setDni("20123456789");
        businessAccountDto.setAccountNumber("BUS-001");
        businessAccountDto.setCreditLimit(50000.0);

        // Setup MessageResponse
//        messageResponse = new MessageResponse();
//        messageResponse.setMessage("Business account deleted successfully");
//        messageResponse.setStatus("SUCCESS");
    }

    @Test
    void testFindAllBusiness_Success() {
        when(businessService.findAllBusiness())
                .thenReturn(Flux.just(businessAccount1, businessAccount2));

        webTestClient.get()
                .uri("/business/findAllBusiness")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Active.class)
                .hasSize(2)
                .value(accounts -> {
                    assert accounts.get(0).getAccountNumber().equals("BUS-001");
                    assert accounts.get(1).getAccountNumber().equals("BUS-002");
                    assert accounts.get(0).getCreditLimit().equals(50000.0);
                    assert accounts.get(1).getCreditLimit().equals(75000.0);
                });

        verify(businessService, times(1)).findAllBusiness();
    }

//    @Test
//    void testFindAllBusiness_EmptyList() {
//        when(businessService.findAllBusiness()).thenReturn(Flux.empty());
//
//        webTestClient.get()
//                .uri("/business/findAllBusiness")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBodyList(Active.class)
//                .hasSize(0);
//
//        verify(businessService, times(1)).findAllBusiness();
//    }
//
//    @Test
//    void testSaveBusiness_Success() {
//        when(businessService.saveBusiness(any(Active.class)))
//                .thenReturn(Mono.just(businessAccount1));
//
//        webTestClient.post()
//                .uri("/business/saveBusiness")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(businessAccountDto)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(Active.class)
//                .value(account -> {
//                    assert account.getDni().equals("20123456789");
//                    assert account.getAccountNumber().equals("BUS-001");
//                    assert account.getCreditLimit().equals(50000.0);
//                    assert account.getTypeCustomer().equals(Constant.BUSINESS_CUSTOMER);
//                    assert account.getStatus().equals(Constant.ACTIVE_ACTIVE);
//                });
//
//        verify(businessService, times(1)).saveBusiness(any(Active.class));
//    }
//
//    @Test
//    void testSaveBusiness_Error() {
//        when(businessService.saveBusiness(any(Active.class)))
//                .thenReturn(Mono.error(new RuntimeException("Error saving business")));
//
//        webTestClient.post()
//                .uri("/business/saveBusiness")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(businessAccountDto)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(Active.class);
//
//        verify(businessService, times(1)).saveBusiness(any(Active.class));
//    }
//
//    @Test
//    void testUpdateBusiness_Success() {
//        Active updatedAccount = new Active();
//        updatedAccount.setId("1");
//        updatedAccount.setAccountNumber("BUS-001");
//        updatedAccount.setDni("20123456789");
//        updatedAccount.setCreditLimit(60000.0);
//        updatedAccount.setStatus(Constant.ACTIVE_ACTIVE);
//        updatedAccount.setModificationDate(new Date());
//
//        when(businessService.updateBusiness(any(Active.class)))
//                .thenReturn(Mono.just(updatedAccount));
//
//        Active updateData = new Active();
//        updateData.setDni("20123456789");
//        updateData.setCreditLimit(60000.0);
//        updateData.setStatus(Constant.ACTIVE_ACTIVE);
//
//        webTestClient.put()
//                .uri("/business/updateBusiness/BUS-001")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(updateData)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(Active.class)
//                .value(account -> {
//                    assert account.getAccountNumber().equals("BUS-001");
//                    assert account.getCreditLimit().equals(60000.0);
//                });
//
//        verify(businessService, times(1)).updateBusiness(any(Active.class));
//    }
//
//    @Test
//    void testUpdateBusiness_NotFound() {
//        when(businessService.updateBusiness(any(Active.class)))
//                .thenReturn(Mono.empty());
//
//        Active updateData = new Active();
//        updateData.setDni("20123456789");
//        updateData.setCreditLimit(60000.0);
//
//        webTestClient.put()
//                .uri("/business/updateBusiness/BUS-999")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(updateData)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody().isEmpty();
//
//        verify(businessService, times(1)).updateBusiness(any(Active.class));
//    }
//
//    @Test
//    void testFindByCustomerBusiness_Success() {
//        when(businessService.findByCustomerBusiness(anyString()))
//                .thenReturn(Flux.just(businessAccount1));
//
//        webTestClient.get()
//                .uri("/business/findByCustomerBusiness/20123456789")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBodyList(Active.class)
//                .hasSize(1)
//                .value(accounts -> {
//                    assert accounts.get(0).getDni().equals("20123456789");
//                    assert accounts.get(0).getAccountNumber().equals("BUS-001");
//                });
//
//        verify(businessService, times(1)).findByCustomerBusiness("20123456789");
//    }
//
//    @Test
//    void testFindByCustomerBusiness_NoAccounts() {
//        when(businessService.findByCustomerBusiness(anyString()))
//                .thenReturn(Flux.empty());
//
//        webTestClient.get()
//                .uri("/business/findByCustomerBusiness/20999999999")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBodyList(Active.class)
//                .hasSize(0);
//
//        verify(businessService, times(1)).findByCustomerBusiness("20999999999");
//    }
//
//    @Test
//    void testFindByAccountNumberBusiness_Success() {
//        when(businessService.findByAccountNumberBusiness(anyString()))
//                .thenReturn(Mono.just(businessAccount1));
//
//        webTestClient.get()
//                .uri("/business/findByAccountNumberBusiness/BUS-001")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(Active.class)
//                .value(account -> {
//                    assert account.getAccountNumber().equals("BUS-001");
//                    assert account.getDni().equals("20123456789");
//                    assert account.getCreditLimit().equals(50000.0);
//                });
//
//        verify(businessService, times(1)).findByAccountNumberBusiness("BUS-001");
//    }
//
//    @Test
//    void testFindByAccountNumberBusiness_NotFound() {
//        when(businessService.findByAccountNumberBusiness(anyString()))
//                .thenReturn(Mono.empty());
//
//        webTestClient.get()
//                .uri("/business/findByAccountNumberBusiness/BUS-999")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody().isEmpty();
//
//        verify(businessService, times(1)).findByAccountNumberBusiness("BUS-999");
//    }
//
////    @Test
////    void testDeleteBusiness_Success() {
////        when(businessService.deleteBusiness(anyString()))
////                .thenReturn(Mono.just(messageResponse));
////
////        webTestClient.delete()
////                .uri("/business/deleteBusiness/BUS-001")
////                .exchange()
////                .expectStatus().isOk()
////                .expectBody(MessageResponse.class)
////                .value(response -> {
////                    assert response.getMessage().equals("Business account deleted successfully");
////                    assert response.getStatus().equals("SUCCESS");
////                });
////
////        verify(businessService, times(1)).deleteBusiness("BUS-001");
////    }
//
//    @Test
//    void testDeleteBusiness_NotFound() {
//        when(businessService.deleteBusiness(anyString()))
//                .thenReturn(Mono.empty());
//
//        webTestClient.delete()
//                .uri("/business/deleteBusiness/BUS-999")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody().isEmpty();
//
//        verify(businessService, times(1)).deleteBusiness("BUS-999");
//    }
//
//    @Test
//    void testFindAllBusiness_WithCircuitBreakerFallback() {
//        when(businessService.findAllBusiness())
//                .thenReturn(Flux.error(new RuntimeException("Service unavailable")));
//
//        webTestClient.get()
//                .uri("/business/findAllBusiness")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBodyList(Active.class);
//
//        verify(businessService, times(1)).findAllBusiness();
//    }
//
//    @Test
//    void testSaveBusiness_WithCircuitBreakerFallback() {
//        when(businessService.saveBusiness(any(Active.class)))
//                .thenReturn(Mono.error(new RuntimeException("Service unavailable")));
//
//        webTestClient.post()
//                .uri("/business/saveBusiness")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(businessAccountDto)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(Active.class);
//
//        verify(businessService, times(1)).saveBusiness(any(Active.class));
//    }
//
//    @Test
//    void testUpdateBusiness_WithCircuitBreakerFallback() {
//        when(businessService.updateBusiness(any(Active.class)))
//                .thenReturn(Mono.error(new RuntimeException("Service unavailable")));
//
//        Active updateData = new Active();
//        updateData.setDni("20123456789");
//        updateData.setCreditLimit(60000.0);
//
//        webTestClient.put()
//                .uri("/business/updateBusiness/BUS-001")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(updateData)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(Active.class);
//
//        verify(businessService, times(1)).updateBusiness(any(Active.class));
//    }
//
//    @Test
//    void testFindByAccountNumberBusiness_WithCircuitBreakerFallback() {
//        when(businessService.findByAccountNumberBusiness(anyString()))
//                .thenReturn(Mono.error(new RuntimeException("Service unavailable")));
//
//        webTestClient.get()
//                .uri("/business/findByAccountNumberBusiness/BUS-001")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(Active.class);
//
//        verify(businessService, times(1)).findByAccountNumberBusiness("BUS-001");
//    }
}