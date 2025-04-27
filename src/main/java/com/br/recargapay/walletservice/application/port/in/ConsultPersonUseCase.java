package com.br.recargapay.walletservice.application.port.in;

import reactor.core.publisher.Mono;

public interface ConsultPersonUseCase {
    Mono<Boolean> existsByDocument(String document);
}
