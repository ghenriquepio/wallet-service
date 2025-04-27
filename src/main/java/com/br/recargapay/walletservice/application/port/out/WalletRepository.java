package com.br.recargapay.walletservice.application.port.out;

import com.br.recargapay.walletservice.domain.model.Wallet;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletRepository {
    Mono<Wallet> save(Wallet wallet);
    Mono<Wallet> findById(UUID walletId);
    Mono<Wallet> updateBalance(Wallet wallet);
    Mono<Void> deleteAll();
}