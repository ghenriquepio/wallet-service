package com.br.recargapay.walletservice.adapter.out.persistence.impl;

import com.br.recargapay.walletservice.adapter.out.persistence.PersonEntity;
import com.br.recargapay.walletservice.adapter.out.persistence.repository.PersonEntityRepository;
import com.br.recargapay.walletservice.application.port.out.PersonRepository;
import com.br.recargapay.walletservice.domain.model.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PersonRepositoryImpl implements PersonRepository {

    private final PersonEntityRepository repository;

    @Override
    public Mono<Person> save(Person person) {
        return repository.save(PersonEntity.fromDomain(person))
                .map(PersonEntity::toDomain);
    }

    @Override
    public Mono<Boolean> existsByDocument(String document) {
        return repository.existsPersonEntityByDocumentNumber(document);
    }

    @Override
    public Mono<Void> deleteAll() {
        return repository.deleteAll();
    }
}