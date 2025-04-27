package com.br.recargapay.walletservice.adapter.out.persistence.repository;

import com.br.recargapay.walletservice.adapter.out.persistence.WalletEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface WalletEntityRepository extends ReactiveCrudRepository<WalletEntity, UUID> {
}