package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.application.port.in.ConsultWalletUseCase;
import com.br.recargapay.walletservice.application.port.out.WalletRepository;
import com.br.recargapay.walletservice.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultWalletService implements ConsultWalletUseCase {

    private final WalletRepository walletRepository;

    @Override
    public Mono<Wallet> findById(UUID id) {
        return walletRepository.findById(id);
    }
}
