package com.br.recargapay.walletservice.application.port.out;

import com.br.recargapay.walletservice.domain.model.Person;
import reactor.core.publisher.Mono;

public interface PersonRepository {
    Mono<Person> save(Person person);
    Mono<Boolean> existsByDocument(String document);
    Mono<Void> deleteAll();
}