package com.br.recargapay.walletservice.application.port.in;

import com.br.recargapay.walletservice.domain.model.Person;
import reactor.core.publisher.Mono;

public interface CreatePersonUseCase {
    Mono<Person> save(Person person);
}
