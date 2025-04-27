package com.br.recargapay.walletservice.application.port.in;

import com.br.recargapay.walletservice.adapter.in.rest.dto.response.WalletHistoricalBalanceResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public interface RetrieveHistoricalWalletBalanceUseCase {
    Mono<WalletHistoricalBalanceResponse> findHistoricalBalanceByWalletId(UUID walletId, LocalDateTime referenceDate);

}
