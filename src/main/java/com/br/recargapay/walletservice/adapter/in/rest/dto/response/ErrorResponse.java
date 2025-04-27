package com.br.recargapay.walletservice.adapter.in.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO representing an error response returned by the API.
 *
 * @param message     A human-readable message describing the error.
 * @param statusCode  The HTTP status code associated with the error.
 */
@Schema(description = "Standard error response")
public record ErrorResponse(

        @Schema(description = "A message describing the error", example = "Wallet not found")
        String message,

        @Schema(description = "HTTP status code", example = "404")
        Integer statusCode

) {}