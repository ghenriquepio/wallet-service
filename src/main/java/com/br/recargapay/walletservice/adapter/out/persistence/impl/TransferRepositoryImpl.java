package com.br.recargapay.walletservice.adapter.out.persistence.impl;

import com.br.recargapay.walletservice.adapter.out.persistence.TransferEntity;
import com.br.recargapay.walletservice.adapter.out.persistence.repository.TransferEntityRepository;
import com.br.recargapay.walletservice.application.port.out.TransferRepository;
import com.br.recargapay.walletservice.domain.model.Transfer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransferRepositoryImpl implements TransferRepository {

    private final TransferEntityRepository transferEntityRepository;

    @Override
    public Mono<BigDecimal> getInboundTransferSumUntil(UUID walletId, LocalDateTime date) {
        return transferEntityRepository.getInboundTransferSumUntil(walletId, date);
    }

    @Override
    public Mono<BigDecimal> getOutboundTransferSumUntil(UUID walletId, LocalDateTime date) {
        return transferEntityRepository.getOutboundTransferSumUntil(walletId, date);
    }

    @Override
    public Mono<Transfer> save(Transfer transfer) {
        return transferEntityRepository.save(TransferEntity.fromDomain(transfer)).map(TransferEntity::toDomain);
    }

    @Override
    public Mono<Void> deleteAll() {
        return transferEntityRepository.deleteAll();
    }
}