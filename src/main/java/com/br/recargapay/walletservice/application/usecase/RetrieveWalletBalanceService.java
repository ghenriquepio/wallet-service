package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.adapter.in.rest.dto.response.WalletBalanceResponse;
import com.br.recargapay.walletservice.application.port.in.ConsultWalletUseCase;
import com.br.recargapay.walletservice.application.port.in.RetrieveWalletBalanceUseCase;
import com.br.recargapay.walletservice.application.port.out.WalletRepository;
import com.br.recargapay.walletservice.domain.exception.WalletNotFoundException;
import com.br.recargapay.walletservice.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.br.recargapay.walletservice.domain.exception.ExceptionEnum.WALLET_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RetrieveWalletBalanceService implements RetrieveWalletBalanceUseCase {

    private final ConsultWalletUseCase consultWalletUseCase;

    @Override
    public Mono<WalletBalanceResponse> findBalanceByWalletId(UUID walletId) {
        return consultWalletUseCase.findById(walletId)
                .switchIfEmpty(Mono.error(new WalletNotFoundException(
                        WALLET_NOT_FOUND.getMessage(), WALLET_NOT_FOUND.getStatusCode()
                )))
                .map(WalletBalanceResponse::fromDomain);
    }
}