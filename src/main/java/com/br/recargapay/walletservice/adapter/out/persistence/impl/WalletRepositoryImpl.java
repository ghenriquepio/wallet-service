package com.br.recargapay.walletservice.adapter.out.persistence.impl;

import com.br.recargapay.walletservice.adapter.out.persistence.WalletEntity;
import com.br.recargapay.walletservice.adapter.out.persistence.repository.WalletEntityRepository;
import com.br.recargapay.walletservice.application.port.out.WalletRepository;
import com.br.recargapay.walletservice.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WalletRepositoryImpl implements WalletRepository{

    private final WalletEntityRepository walletRepository;

    @Override
    public Mono<Wallet> save(Wallet wallet) {
        return walletRepository.save(WalletEntity.fromDomain(wallet)).map(WalletEntity::toDomain);
    }

    @Override
    public Mono<Wallet> findById(UUID walletId) {
        return walletRepository.findById(walletId).map(WalletEntity::toDomain);
    }

    @Override
    public Mono<Wallet> updateBalance(Wallet wallet) {
        return walletRepository.findById(wallet.getId()).flatMap(
                walletEntity -> {
                    walletEntity.setBalance(wallet.getBalance());
                    return walletRepository.save(walletEntity).map(WalletEntity::toDomain);
                }

        );
    }

    @Override
    public Mono<Void> deleteAll() {
        return walletRepository.deleteAll();
    }
}