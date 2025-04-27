package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.application.port.out.PersonRepository;
import org.junit.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class ConsultPersonServiceTest {

    @Test
    public void test_returns_true_when_person_with_document_exists() {
        PersonRepository personRepository = Mockito.mock(PersonRepository.class);
        ConsultPersonService consultPersonService = new ConsultPersonService(personRepository);
        String document = "12345678900";
    
        Mockito.when(personRepository.existsByDocument(document)).thenReturn(Mono.just(true));
    
        Mono<Boolean> result = consultPersonService.existsByDocument(document);
    
        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete();
    
        Mockito.verify(personRepository).existsByDocument(document);
    }

    @Test
    public void test_handles_empty_document_string() {
        PersonRepository personRepository = Mockito.mock(PersonRepository.class);
        ConsultPersonService consultPersonService = new ConsultPersonService(personRepository);
        String emptyDocument = "";
    
        Mockito.when(personRepository.existsByDocument(emptyDocument)).thenReturn(Mono.just(false));
    
        Mono<Boolean> result = consultPersonService.existsByDocument(emptyDocument);
    
        StepVerifier.create(result)
            .expectNext(false)
            .verifyComplete();
    
        Mockito.verify(personRepository).existsByDocument(emptyDocument);
    }

    @Test
    public void test_handles_null_document_parameter() {
        PersonRepository personRepository = Mockito.mock(PersonRepository.class);
        ConsultPersonService consultPersonService = new ConsultPersonService(personRepository);
        String nullDocument = null;
    
        Mockito.when(personRepository.existsByDocument(nullDocument)).thenReturn(Mono.error(new IllegalArgumentException("Document cannot be null")));
    
        Mono<Boolean> result = consultPersonService.existsByDocument(nullDocument);
    
        StepVerifier.create(result)
            .expectError(IllegalArgumentException.class)
            .verify();
    
        Mockito.verify(personRepository).existsByDocument(nullDocument);
    }
}