package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.adapter.in.rest.dto.request.OnboardingRequest;
import com.br.recargapay.walletservice.application.port.in.*;
import com.br.recargapay.walletservice.domain.exception.PersonExistsException;
import com.br.recargapay.walletservice.domain.model.Person;
import com.br.recargapay.walletservice.domain.model.Wallet;
import org.junit.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OnboardingServiceTest {

    @Test
    public void test_successful_onboarding_with_valid_data() {

        ConsultPersonUseCase consultPersonUseCase = Mockito.mock(ConsultPersonService.class);
        CreatePersonUseCase createPersonUseCase = Mockito.mock(CreatePersonUseCase.class);
        CreateWalletUseCase createWalletUseCase = Mockito.mock(CreateWalletUseCase.class);
        OnboardingUseCase onboardingService = new OnboardingService(createPersonUseCase, createWalletUseCase, consultPersonUseCase);

        OnboardingRequest request = new OnboardingRequest(
            "John Doe",
            "12345678901",
            "john.doe@example.com",
            "11999999999"
        );
    
        Person person = request.toDomain();
        Person savedPerson = new Person(
            UUID.randomUUID(),
            person.getFullName(),
            person.getDocumentNumber(),
            person.getEmail(),
            person.getPhoneNumber(),
            person.getCreatedAt(),
            LocalDateTime.now()
        );
    
        Wallet savedWallet = Wallet.builder()
            .id(UUID.randomUUID())
            .ownerId(savedPerson.getId())
            .balance(BigDecimal.ZERO)
            .statusId(1)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    
        when(consultPersonUseCase.existsByDocument(person.getDocumentNumber())).thenReturn(Mono.just(false));
        when(createPersonUseCase.save(any(Person.class))).thenReturn(Mono.just(savedPerson));
        when(createWalletUseCase.create(savedPerson.getId())).thenReturn(Mono.just(savedWallet));
    
        StepVerifier.create(onboardingService.execute(request))
            .assertNext(response -> {
                assertNotNull(response);
                assertEquals(savedPerson.getId(), response.person().id());
                assertEquals(savedPerson.getFullName(), response.person().fullName());
                assertEquals(savedPerson.getDocumentNumber(), response.person().documentNumber());
                assertEquals(savedPerson.getEmail(), response.person().email());
                assertEquals(savedPerson.getPhoneNumber(), response.person().phoneNumber());
            
                assertEquals(savedWallet.getId(), response.wallet().id());
                assertEquals(savedWallet.getOwnerId(), response.wallet().ownerId());
                assertEquals(savedWallet.getBalance(), response.wallet().balance());
                assertEquals(savedWallet.getStatusId(), response.wallet().statusId());
            })
            .verifyComplete();
    
        verify(consultPersonUseCase).existsByDocument(person.getDocumentNumber());
        verify(createPersonUseCase).save(any(Person.class));
        verify(createWalletUseCase).create(savedPerson.getId());
    }

    @Test
    public void test_person_already_exists_with_same_document() {

        ConsultPersonUseCase consultPersonUseCase = Mockito.mock(ConsultPersonService.class);
        CreatePersonUseCase createPersonUseCase = Mockito.mock(CreatePersonUseCase.class);
        CreateWalletUseCase createWalletUseCase = Mockito.mock(CreateWalletUseCase.class);
        OnboardingUseCase onboardingService = new OnboardingService(createPersonUseCase, createWalletUseCase, consultPersonUseCase);

        OnboardingRequest request = new OnboardingRequest(
            "John Doe",
            "12345678901",
            "john.doe@example.com",
            "11999999999"
        );
    
        Person person = request.toDomain();
    
        when(consultPersonUseCase.existsByDocument(person.getDocumentNumber())).thenReturn(Mono.just(true));

        StepVerifier.create(onboardingService.execute(request))
            .expectErrorSatisfies(error -> {
                assertTrue(error instanceof PersonExistsException);
                PersonExistsException exception = (PersonExistsException) error;
                assertNotNull(exception.getMessage());
                assertNotNull(exception.getCode());
            })
            .verify();
    
        verify(consultPersonUseCase).existsByDocument(person.getDocumentNumber());
        verify(createPersonUseCase, never()).save(any(Person.class));
        verify(createWalletUseCase, never()).create(any(UUID.class));
    }

    @Test
    public void test_onboarding_with_minimum_valid_input() {

        ConsultPersonUseCase consultPersonUseCase = Mockito.mock(ConsultPersonService.class);
        CreatePersonUseCase createPersonUseCase = Mockito.mock(CreatePersonUseCase.class);
        CreateWalletUseCase createWalletUseCase = Mockito.mock(CreateWalletUseCase.class);
        OnboardingUseCase onboardingService = new OnboardingService(createPersonUseCase, createWalletUseCase, consultPersonUseCase);

        OnboardingRequest request = new OnboardingRequest(
            "A",
            "1",
            "a@b.c",
            "1"
        );
    
        Person person = request.toDomain();
        Person savedPerson = new Person(
            UUID.randomUUID(),
            person.getFullName(),
            person.getDocumentNumber(),
            person.getEmail(),
            person.getPhoneNumber(),
            person.getCreatedAt(),
            LocalDateTime.now()
        );
    
        Wallet savedWallet = Wallet.builder()
            .id(UUID.randomUUID())
            .ownerId(savedPerson.getId())
            .balance(BigDecimal.ZERO)
            .statusId(1)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    
        when(consultPersonUseCase.existsByDocument(person.getDocumentNumber())).thenReturn(Mono.just(false));
        when(createPersonUseCase.save(any(Person.class))).thenReturn(Mono.just(savedPerson));
        when(createWalletUseCase.create(savedPerson.getId())).thenReturn(Mono.just(savedWallet));
    
        StepVerifier.create(onboardingService.execute(request))
            .assertNext(response -> {
                assertNotNull(response);
                assertEquals("A", response.person().fullName());
                assertEquals("1", response.person().documentNumber());
                assertEquals("a@b.c", response.person().email());
                assertEquals("1", response.person().phoneNumber());
            
                assertNotNull(response.wallet());
                assertEquals(savedPerson.getId(), response.wallet().ownerId());
            })
            .verifyComplete();
    
        verify(consultPersonUseCase).existsByDocument("1");
        verify(createPersonUseCase).save(any(Person.class));
        verify(createWalletUseCase).create(savedPerson.getId());
    }
}