package com.br.recargapay.walletservice.adapter.out.persistence.repository;

import com.br.recargapay.walletservice.adapter.out.persistence.PersonEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PersonEntityRepository extends ReactiveCrudRepository<PersonEntity, UUID> {

    Mono<Boolean> existsPersonEntityByDocumentNumber(String documentNumber);
}