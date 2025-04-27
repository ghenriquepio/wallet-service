package com.br.recargapay.walletservice.adapter.out.persistence.repository;

import com.br.recargapay.walletservice.adapter.out.persistence.TransferEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TransferEntityRepository extends ReactiveCrudRepository<TransferEntity, UUID> {

    @Query("""
        SELECT COALESCE(SUM(amount), 0)
        FROM transfer
        WHERE wallet_id_to = :walletId AND created_at <= :date
    """)
    Mono<BigDecimal> getInboundTransferSumUntil(UUID walletId, LocalDateTime date);

    @Query("""
        SELECT COALESCE(SUM(amount), 0)
        FROM transfer
        WHERE wallet_id_from = :walletId AND created_at <= :date
    """)
    Mono<BigDecimal> getOutboundTransferSumUntil(UUID walletId, LocalDateTime date);
}