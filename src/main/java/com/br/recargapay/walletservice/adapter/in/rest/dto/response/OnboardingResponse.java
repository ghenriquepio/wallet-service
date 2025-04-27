package com.br.recargapay.walletservice.adapter.in.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response body returned after successful onboarding of a person and creation of a wallet.
 *
 * @param person The newly created person information.
 * @param wallet The newly created wallet information.
 */
@Schema(description = "Response returned after onboarding a new user and creating their wallet")
public record OnboardingResponse(

        @Schema(description = "Details of the created person")
        PersonResponse person,

        @Schema(description = "Details of the created wallet")
        WalletResponse wallet

) {}