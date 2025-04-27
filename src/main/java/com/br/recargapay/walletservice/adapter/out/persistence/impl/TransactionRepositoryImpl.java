package com.br.recargapay.walletservice.adapter.out.persistence.impl;

import com.br.recargapay.walletservice.adapter.out.persistence.TransactionEntity;
import com.br.recargapay.walletservice.adapter.out.persistence.repository.TransactionEntityRepository;
import com.br.recargapay.walletservice.application.port.out.TransactionRepository;
import com.br.recargapay.walletservice.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionEntityRepository transactionEntityRepository;

    @Override
    public Mono<BigDecimal> getTransactionBalanceUntil(UUID walletId, LocalDateTime date) {
        return transactionEntityRepository.getTransactionBalanceUntil(walletId, date);
    }

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        return transactionEntityRepository.save(TransactionEntity.fromDomain(transaction)).map(TransactionEntity::toDomain);
    }

    @Override
    public Mono<Void> deleteAll() {
        return transactionEntityRepository.deleteAll();
    }
}