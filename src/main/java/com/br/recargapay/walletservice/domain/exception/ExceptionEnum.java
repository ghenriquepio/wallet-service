package com.br.recargapay.walletservice.domain.exception;

import jakarta.ws.rs.core.Response;
import lombok.Getter;

@Getter
public enum ExceptionEnum {
    PERSON_EXISTS("Person already exists", Response.Status.BAD_REQUEST.getStatusCode()),
    WALLET_NOT_FOUND("Wallet not found", Response.Status.NOT_FOUND.getStatusCode()),
    INSUFFICIENT_FUNDS("Insufficient funds", Response.Status.BAD_REQUEST.getStatusCode());

    final String message;
    final Integer statusCode;

    ExceptionEnum(String message, Integer statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
