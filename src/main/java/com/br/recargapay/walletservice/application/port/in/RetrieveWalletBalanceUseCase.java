package com.br.recargapay.walletservice.application.port.in;

import com.br.recargapay.walletservice.adapter.in.rest.dto.response.WalletBalanceResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RetrieveWalletBalanceUseCase {
    Mono<WalletBalanceResponse> findBalanceByWalletId(UUID walletId);
}