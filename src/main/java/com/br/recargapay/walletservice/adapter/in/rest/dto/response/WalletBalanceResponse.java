package com.br.recargapay.walletservice.adapter.in.rest.dto.response;

import com.br.recargapay.walletservice.domain.model.Wallet;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response object representing the current balance of a wallet.
 *
 * @param id       The unique identifier of the wallet.
 * @param ownerId  The unique identifier of the wallet owner.
 * @param balance  The current balance in the wallet.
 */
@Schema(description = "Response containing the current balance of a wallet.")
public record WalletBalanceResponse(

        @Schema(description = "Unique ID of the wallet", example = "1d46fa17-4b6c-4201-87f5-3b9ecddf3c0a")
        UUID id,

        @Schema(description = "Unique ID of the wallet owner", example = "3a8f7b5d-93d0-4e2e-a084-1f5cbe385d9d")
        UUID ownerId,

        @Schema(description = "Current balance in the wallet", example = "250.75")
        BigDecimal balance

) {
    /**
     * Converts a domain Wallet object into a WalletBalanceResponse.
     *
     * @param wallet The domain Wallet object.
     * @return A WalletBalanceResponse containing wallet details.
     */
    public static WalletBalanceResponse fromDomain(Wallet wallet) {
        return new WalletBalanceResponse(
                wallet.getId(),
                wallet.getOwnerId(),
                wallet.getBalance()
        );
    }
}