package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.adapter.in.rest.dto.request.TransferFundsRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.response.TransferResponse;
import com.br.recargapay.walletservice.application.port.in.TransferFundsUseCase;
import com.br.recargapay.walletservice.application.port.out.TransferRepository;
import com.br.recargapay.walletservice.application.port.out.WalletRepository;
import com.br.recargapay.walletservice.domain.exception.InsufficientFundsException;
import com.br.recargapay.walletservice.domain.exception.WalletNotFoundException;
import com.br.recargapay.walletservice.domain.model.Transfer;
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
public class TransferFoundsService implements TransferFundsUseCase {

    private final TransferRepository transferRepository;
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public Mono<TransferResponse> transferFunds(UUID fromWalletId, TransferFundsRequest request) {
        UUID toId = request.toWalletId();
        BigDecimal amount = request.amount();

        return Mono.zip(walletRepository.findById(fromWalletId), walletRepository.findById(toId))
                .switchIfEmpty(Mono.error(new WalletNotFoundException(WALLET_NOT_FOUND.getMessage(), WALLET_NOT_FOUND.getStatusCode())))
                .flatMap(tuple -> {
                    Wallet fromWallet = tuple.getT1();
                    Wallet toWallet = tuple.getT2();

                    if (fromWallet.getBalance().compareTo(amount) < 0) {
                        return Mono.error(new InsufficientFundsException(INSUFFICIENT_FUNDS.getMessage(), INSUFFICIENT_FUNDS.getStatusCode()));
                    }

                    Transfer transfer = request.toDomain(fromWalletId);

                    return transferRepository.save(transfer)
                            .then(Mono.defer(() -> {
                                fromWallet.setBalance(fromWallet.getBalance().subtract(amount));
                                toWallet.setBalance(toWallet.getBalance().add(amount));

                                return Mono.zip(
                                        walletRepository.updateBalance(fromWallet),
                                        walletRepository.updateBalance(toWallet)
                                );
                            }))
                            .map(saved -> new TransferResponse(
                                    fromWalletId, toId, amount
                            ));
                });
    }

}
