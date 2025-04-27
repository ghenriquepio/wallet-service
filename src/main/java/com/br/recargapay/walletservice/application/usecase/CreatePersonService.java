package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.application.port.in.CreatePersonUseCase;
import com.br.recargapay.walletservice.application.port.out.PersonRepository;
import com.br.recargapay.walletservice.domain.model.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreatePersonService implements CreatePersonUseCase {

    private final PersonRepository personRepository;

    @Override
    public Mono<Person> save(Person person) {
        return personRepository.save(person);
    }
}
