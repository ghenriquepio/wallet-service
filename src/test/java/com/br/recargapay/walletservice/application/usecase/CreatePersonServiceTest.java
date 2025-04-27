package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.application.port.out.PersonRepository;
import com.br.recargapay.walletservice.domain.model.Person;
import org.junit.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

public class CreatePersonServiceTest {

    @Test
    public void test_save_valid_person_successfully() {

        Person person = Person.builder()
            .id(UUID.randomUUID())
            .fullName("John Doe")
            .documentNumber("12345678900")
            .email("john.doe@example.com")
            .phoneNumber("11999999999")
            .build();
    
        PersonRepository personRepository = Mockito.mock(PersonRepository.class);
        Mockito.when(personRepository.save(person)).thenReturn(Mono.just(person));
    
        CreatePersonService createPersonService = new CreatePersonService(personRepository);
    
        Mono<Person> result = createPersonService.save(person);
    
        StepVerifier.create(result)
            .expectNext(person)
            .verifyComplete();
    
        Mockito.verify(personRepository).save(person);
    }

    @Test
    public void test_save_person_with_null_fields() {

        Person personWithNullFields = Person.builder()
            .id(UUID.randomUUID())
            .fullName(null)
            .documentNumber("12345678900")
            .email(null)
            .phoneNumber(null)
            .build();
    
        PersonRepository personRepository = Mockito.mock(PersonRepository.class);
        Mockito.when(personRepository.save(personWithNullFields)).thenReturn(Mono.just(personWithNullFields));
    
        CreatePersonService createPersonService = new CreatePersonService(personRepository);
    
        Mono<Person> result = createPersonService.save(personWithNullFields);
    
        StepVerifier.create(result)
            .expectNext(personWithNullFields)
            .verifyComplete();
    
        Mockito.verify(personRepository).save(personWithNullFields);
    }

    @Test
    public void test_save_person_with_empty_strings() {
        Person personWithEmptyStrings = Person.builder()
            .id(UUID.randomUUID())
            .fullName("")
            .documentNumber("")
            .email("")
            .phoneNumber("")
            .build();
    
        PersonRepository personRepository = Mockito.mock(PersonRepository.class);
        Mockito.when(personRepository.save(personWithEmptyStrings)).thenReturn(Mono.just(personWithEmptyStrings));
    
        CreatePersonService createPersonService = new CreatePersonService(personRepository);
    
        Mono<Person> result = createPersonService.save(personWithEmptyStrings);
    
        StepVerifier.create(result)
            .expectNext(personWithEmptyStrings)
            .verifyComplete();
    
        Mockito.verify(personRepository).save(personWithEmptyStrings);
    }
}