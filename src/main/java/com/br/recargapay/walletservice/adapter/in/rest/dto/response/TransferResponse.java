package com.br.recargapay.walletservice.adapter.in.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response object representing the result of a wallet-to-wallet transfer.
 *
 * @param fromWalletId     The UUID of the wallet that sent the funds.
 * @param toWalletId       The UUID of the wallet that received the funds.
 * @param transferredAmount The amount of money transferred between the wallets.
 */
@Schema(description = "Response body representing a successful transfer between wallets.")
public record TransferResponse(

        @Schema(description = "ID of the source wallet", example = "c7e343b4-e2c1-4c25-b2f3-b5292b2146d0")
        UUID fromWalletId,

        @Schema(description = "ID of the destination wallet", example = "f1e43b21-c2c7-4c56-a118-b1671d52e624")
        UUID toWalletId,

        @Schema(description = "Amount transferred", example = "100.00")
        BigDecimal transferredAmount

) {}