package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.adapter.in.rest.dto.request.DepositFundsRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.response.WalletResponse;
import com.br.recargapay.walletservice.application.port.in.DepositFundsUseCase;
import com.br.recargapay.walletservice.application.port.out.TransactionRepository;
import com.br.recargapay.walletservice.application.port.out.WalletRepository;
import com.br.recargapay.walletservice.domain.exception.WalletNotFoundException;
import com.br.recargapay.walletservice.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

import static com.br.recargapay.walletservice.domain.exception.ExceptionEnum.WALLET_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DepositFundsService implements DepositFundsUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Mono<WalletResponse> deposit(UUID walletId, DepositFundsRequest request) {
        BigDecimal amount = request.amount();

        return walletRepository.findById(walletId)
                .switchIfEmpty(Mono.error(new WalletNotFoundException(WALLET_NOT_FOUND.getMessage(), WALLET_NOT_FOUND.getStatusCode())))
                .flatMap(wallet -> {
                    wallet.setBalance(wallet.getBalance().add(amount));
                    Transaction transaction = request.toDomain(wallet.getId());

                    return Mono.zip(
                            walletRepository.updateBalance(wallet),
                            transactionRepository.save(transaction)
                    ).map(tuple -> WalletResponse.fromDomain(tuple.getT1()));
                });
    }
}
