package com.br.recargapay.walletservice.adapter.in.rest.dto.request;

import com.br.recargapay.walletservice.domain.model.Transaction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO representing the request to withdraw funds from a wallet.
 *
 * @param amount The amount of money to be withdrawn.
 */
@Schema(description = "Request body to withdraw funds from a wallet.")
public record WithdrawFundsRequest(

        @NotNull(message = "Amount must not be null")
        @Positive(message = "Amount must be greater than zero")
        @Schema(description = "Amount to withdraw", example = "75.00", required = true)
        BigDecimal amount

) {
    /**
     * Converts this DTO into a {@link Transaction} domain object for withdrawal.
     *
     * @param walletId the ID of the wallet from which funds will be withdrawn
     * @return a new {@link Transaction} representing the withdrawal
     */
    public Transaction toDomain(UUID walletId) {
        return Transaction.builder()
                .walletId(walletId)
                .type("WITHDRAW")
                .amount(amount)
                .description("Withdraw")
                .build();
    }
}