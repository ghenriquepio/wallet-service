package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.application.port.in.ConsultPersonUseCase;
import com.br.recargapay.walletservice.application.port.out.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ConsultPersonService implements ConsultPersonUseCase {

    private final PersonRepository personRepository;

    @Override
    public Mono<Boolean> existsByDocument(String document) {
        return personRepository.existsByDocument(document);
    }
}