package com.br.recargapay.walletservice.adapter.in.rest.dto.response;

import com.br.recargapay.walletservice.domain.model.Wallet;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response object representing wallet details including balance and status.
 *
 * @param id         The unique identifier of the wallet.
 * @param ownerId    The identifier of the wallet's owner.
 * @param balance    The current balance of the wallet.
 * @param statusId   The status identifier of the wallet (e.g., active, blocked).
 * @param createdAt  Timestamp of when the wallet was created.
 * @param updatedAt  Timestamp of the last wallet update.
 */
@Schema(description = "Response with wallet details including balance and status.")
public record WalletResponse(

        @Schema(description = "Unique ID of the wallet", example = "1d9a1a6d-5b0e-4c6a-bcd6-6dcf7d9a271e")
        UUID id,

        @NotNull(message = "Owner ID is required")
        @Schema(description = "Owner ID associated with the wallet", example = "2e95f1cc-8927-4bd7-b2b5-2b1987b6dcef", required = true)
        UUID ownerId,

        @NotNull(message = "Balance is required")
        @PositiveOrZero(message = "Balance must be zero or positive")
        @Schema(description = "Current balance of the wallet", example = "120.00", required = true)
        BigDecimal balance,

        @NotNull(message = "Status ID is required")
        @Schema(description = "Current status ID of the wallet", example = "1", required = true)
        Integer statusId,

        @Schema(description = "Date and time when the wallet was created", example = "2024-03-01T10:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Date and time of the last update to the wallet", example = "2024-04-15T15:30:00")
        LocalDateTime updatedAt

) {
    /**
     * Creates a {@link WalletResponse} from a {@link Wallet} domain model.
     *
     * @param domain The Wallet domain object.
     * @return A mapped WalletResponse.
     */
    public static WalletResponse fromDomain(Wallet domain) {
        return new WalletResponse(
                domain.getId(),
                domain.getOwnerId(),
                domain.getBalance(),
                domain.getStatusId(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}