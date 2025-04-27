package com.br.recargapay.walletservice.adapter.in.rest.dto.request;

import com.br.recargapay.walletservice.domain.model.Transaction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO representing the payload required to deposit funds into a wallet.
 *
 * <p>This request is used in the deposit endpoint to initiate a fund deposit
 * into a specific wallet. It contains validation to ensure the amount is present and positive.</p>
 *
 * @param amount The monetary amount to deposit. Must be positive and not null.
 */
@Schema(description = "Request body to deposit funds into a wallet.")
public record DepositFundsRequest(

        @Schema(description = "Amount to be deposited", example = "100.00", required = true)
        @NotNull(message = "Amount must not be null")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount

) {
    /**
     * Converts this DTO to a {@link Transaction} domain object representing a deposit.
     *
     * @param walletId the ID of the wallet into which funds will be deposited
     * @return a {@link Transaction} object representing the deposit operation
     */
    public Transaction toDomain(UUID walletId) {
        return Transaction.builder()
                .walletId(walletId)
                .type("DEPOSIT")
                .amount(amount)
                .description("Deposit")
                .build();
    }
}