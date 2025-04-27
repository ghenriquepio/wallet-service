package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.adapter.in.rest.dto.response.WalletHistoricalBalanceResponse;
import com.br.recargapay.walletservice.application.port.in.RetrieveHistoricalWalletBalanceUseCase;
import com.br.recargapay.walletservice.application.port.out.TransactionRepository;
import com.br.recargapay.walletservice.application.port.out.TransferRepository;
import com.br.recargapay.walletservice.application.port.out.WalletRepository;
import com.br.recargapay.walletservice.domain.exception.WalletNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.br.recargapay.walletservice.domain.exception.ExceptionEnum.WALLET_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RetrieveHistoricalWalletBalanceService implements RetrieveHistoricalWalletBalanceUseCase {

    private final TransactionRepository transactionRepository;
    private final TransferRepository transferRepository;
    private final WalletRepository walletRepository;

    @Override
    public Mono<WalletHistoricalBalanceResponse> findHistoricalBalanceByWalletId(UUID walletId, LocalDateTime referenceDate) {
        return walletRepository.findById(walletId)
                .switchIfEmpty(Mono.error(new WalletNotFoundException(WALLET_NOT_FOUND.getMessage(), WALLET_NOT_FOUND.getStatusCode())))
                .flatMap(wallet -> {
                    Mono<BigDecimal> transactionSum = transactionRepository.getTransactionBalanceUntil(walletId, referenceDate);
                    Mono<BigDecimal> inboundSum = transferRepository.getInboundTransferSumUntil(walletId, referenceDate);
                    Mono<BigDecimal> outboundSum = transferRepository.getOutboundTransferSumUntil(walletId, referenceDate);

                    return Mono.zip(transactionSum, inboundSum, outboundSum)
                            .map(tuple -> {
                                BigDecimal total = tuple.getT1()
                                        .add(tuple.getT2())
                                        .subtract(tuple.getT3());

                                return WalletHistoricalBalanceResponse.fromDomain(wallet, referenceDate, total);
                            });
                });
    }
}