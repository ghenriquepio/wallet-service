package com.br.recargapay.walletservice.adapter.out.persistence.repository;

import com.br.recargapay.walletservice.adapter.out.persistence.TransactionEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TransactionEntityRepository extends ReactiveCrudRepository<TransactionEntity, UUID> {

    @Query("""
        SELECT
            COALESCE(SUM(CASE
                WHEN type = 'DEPOSIT' THEN amount
                WHEN type = 'WITHDRAW' THEN -amount
                ELSE 0
            END), 0)
        FROM transaction
        WHERE wallet_id = :walletId AND created_at <= :date
    """)
    Mono<BigDecimal> getTransactionBalanceUntil(UUID walletId, LocalDateTime date);
}