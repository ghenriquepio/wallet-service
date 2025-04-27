package com.br.recargapay.walletservice.application.port.in;

import com.br.recargapay.walletservice.adapter.in.rest.dto.request.DepositFundsRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.response.WalletResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DepositFundsUseCase {
    Mono<WalletResponse> deposit(UUID walletId, DepositFundsRequest request);
}