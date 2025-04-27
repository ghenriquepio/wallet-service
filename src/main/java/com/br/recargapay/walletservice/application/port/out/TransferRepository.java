package com.br.recargapay.walletservice.application.port.out;

import com.br.recargapay.walletservice.domain.model.Transfer;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TransferRepository {
    Mono<BigDecimal> getInboundTransferSumUntil(UUID walletId, LocalDateTime date);
    Mono<BigDecimal> getOutboundTransferSumUntil(UUID walletId, LocalDateTime date);
    Mono<Transfer> save(Transfer transfer);
    Mono<Void> deleteAll();
}