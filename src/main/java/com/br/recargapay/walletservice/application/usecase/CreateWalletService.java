package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.application.port.in.CreateWalletUseCase;
import com.br.recargapay.walletservice.application.port.out.WalletRepository;
import com.br.recargapay.walletservice.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateWalletService implements CreateWalletUseCase {

    private final WalletRepository walletRepository;

    @Override
    public Mono<Wallet> create(UUID ownerId) {
        Wallet wallet = Wallet.builder()
                .ownerId(ownerId)
                .statusId(1)
                .createdAt(LocalDateTime.now())
                .build();

        return walletRepository.save(wallet);
    }

}