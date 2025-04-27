package com.br.recargapay.walletservice.application.port.out;

import com.br.recargapay.walletservice.domain.model.Transaction;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TransactionRepository {
    Mono<BigDecimal> getTransactionBalanceUntil(UUID walletId, LocalDateTime date);
    Mono<Transaction> save(Transaction transaction);
    Mono<Void> deleteAll();
}