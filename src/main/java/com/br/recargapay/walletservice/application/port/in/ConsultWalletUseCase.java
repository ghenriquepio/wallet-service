package com.br.recargapay.walletservice.application.port.in;

import com.br.recargapay.walletservice.domain.model.Wallet;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ConsultWalletUseCase {
    Mono<Wallet> findById(UUID id);
}