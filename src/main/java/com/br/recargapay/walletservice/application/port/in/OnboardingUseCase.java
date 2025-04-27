package com.br.recargapay.walletservice.application.port.in;

import com.br.recargapay.walletservice.adapter.in.rest.dto.request.OnboardingRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.response.OnboardingResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OnboardingUseCase {
    Mono<OnboardingResponse> execute(OnboardingRequest command);
}