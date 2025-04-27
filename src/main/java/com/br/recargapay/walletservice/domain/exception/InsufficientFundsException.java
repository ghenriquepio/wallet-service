package com.br.recargapay.walletservice.domain.exception;

import lombok.Getter;

@Getter
public class InsufficientFundsException extends RuntimeException {

    final String message;
    final Integer code;

    public InsufficientFundsException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }
}
