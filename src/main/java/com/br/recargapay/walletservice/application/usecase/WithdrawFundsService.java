package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.adapter.in.rest.dto.request.WithdrawFundsRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.response.WalletResponse;
import com.br.recargapay.walletservice.application.port.in.WithdrawFundsUseCase;
import com.br.recargapay.walletservice.application.port.out.TransactionRepository;
import com.br.recargapay.walletservice.application.port.out.WalletRepository;
import com.br.recargapay.walletservice.domain.exception.InsufficientFundsException;
import com.br.recargapay.walletservice.domain.exception.WalletNotFoundException;
import com.br.recargapay.walletservice.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

import static com.br.recargapay.walletservice.domain.exception.ExceptionEnum.INSUFFICIENT_FUNDS;
import static com.br.recargapay.walletservice.domain.exception.ExceptionEnum.WALLET_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class WithdrawFundsService implements WithdrawFundsUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Mono<WalletResponse> withdraw(UUID walletId, WithdrawFundsRequest request) {
        return walletRepository.findById(walletId)
                .switchIfEmpty(Mono.error(new WalletNotFoundException(
                        WALLET_NOT_FOUND.getMessage(),
                        WALLET_NOT_FOUND.getStatusCode()
                )))
                .flatMap(wallet -> validateFunds(wallet, request.amount())
                        .then(transactionRepository.save(request.toDomain(wallet.getId())))
                        .then(Mono.defer(() -> {
                            wallet.setBalance(wallet.getBalance().subtract(request.amount()));
                            return walletRepository.updateBalance(wallet);
                        }))
                )
                .map(WalletResponse::fromDomain);
    }

    private Mono<Void> validateFunds(Wallet wallet, BigDecimal amount) {
        if (wallet.getBalance().compareTo(amount) < 0) {
            return Mono.error(new InsufficientFundsException(INSUFFICIENT_FUNDS.getMessage(), INSUFFICIENT_FUNDS.getStatusCode()));
        }
        return Mono.empty();
    }
}