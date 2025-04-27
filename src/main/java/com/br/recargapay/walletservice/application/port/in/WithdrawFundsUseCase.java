package com.br.recargapay.walletservice.application.port.in;

import com.br.recargapay.walletservice.adapter.in.rest.dto.request.WithdrawFundsRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.response.WalletResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface WithdrawFundsUseCase {
    Mono<WalletResponse> withdraw(UUID walletId, WithdrawFundsRequest request);
}