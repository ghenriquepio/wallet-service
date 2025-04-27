package com.br.recargapay.walletservice.adapter.in.rest.dto.request;

import com.br.recargapay.walletservice.domain.model.Transfer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO representing the request to transfer funds from one wallet to another.
 *
 * @param toWalletId ID of the wallet that will receive the funds.
 * @param amount The amount of money to be transferred.
 */
@Schema(description = "Request body to transfer funds from one wallet to another.")
public record TransferFundsRequest(

        @NotNull(message = "Target wallet ID is required")
        @Schema(description = "UUID of the recipient wallet", example = "e9d2c4d2-7f1d-4d7e-bbbc-dcf6c2210c9a", required = true)
        UUID toWalletId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        @Schema(description = "Amount to transfer", example = "150.00", required = true)
        BigDecimal amount

) {
    /**
     * Converts this DTO into a {@link Transfer} domain object.
     *
     * @param fromWalletId the ID of the wallet from which funds will be transferred
     * @return a new {@link Transfer} instance with populated data
     */
    public Transfer toDomain(UUID fromWalletId) {
        return Transfer.builder()
                .walletIdFrom(fromWalletId)
                .walletIdTo(toWalletId)
                .amount(amount)
                .description("Transfer between wallets")
                .build();
    }
}