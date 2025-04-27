package com.br.recargapay.walletservice.adapter.in.rest.dto.response;

import com.br.recargapay.walletservice.domain.model.Wallet;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response object representing the historical balance of a wallet at a specific point in time.
 *
 * @param walletId      The unique identifier of the wallet.
 * @param ownerId       The unique identifier of the wallet owner.
 * @param referenceDate The reference date and time for the historical balance.
 * @param balance       The wallet balance at the reference date.
 */
@Schema(description = "Response with the wallet balance at a specific point in time.")
public record WalletHistoricalBalanceResponse(

        @Schema(description = "Unique ID of the wallet", example = "9a97d5fc-372e-4c26-8789-5e1e2ebf9842")
        UUID walletId,

        @Schema(description = "Unique ID of the wallet owner", example = "6b712c9d-bfb5-4a9c-b6bc-32acb54ec563")
        UUID ownerId,

        @Schema(description = "Date and time for which the historical balance is calculated", example = "2024-04-01T12:00:00")
        LocalDateTime referenceDate,

        @Schema(description = "Wallet balance at the reference date", example = "150.00")
        BigDecimal balance

) {
    /**
     * Converts a Wallet domain object and reference date into a WalletHistoricalBalanceResponse.
     *
     * @param wallet         The Wallet domain object.
     * @param referenceDate  The date and time of the historical balance check.
     * @param balance        The calculated historical balance.
     * @return A populated WalletHistoricalBalanceResponse.
     */
    public static WalletHistoricalBalanceResponse fromDomain(Wallet wallet, LocalDateTime referenceDate, BigDecimal balance) {
        return new WalletHistoricalBalanceResponse(
                wallet.getId(),
                wallet.getOwnerId(),
                referenceDate,
                balance
        );
    }
}