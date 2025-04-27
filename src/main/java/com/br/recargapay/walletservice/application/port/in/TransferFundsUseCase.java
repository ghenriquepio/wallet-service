package com.br.recargapay.walletservice.application.port.in;

import com.br.recargapay.walletservice.adapter.in.rest.dto.request.TransferFundsRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.response.TransferResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TransferFundsUseCase {
    Mono<TransferResponse> transferFunds(UUID fromWalletId, TransferFundsRequest request);
}