package com.br.recargapay.walletservice.domain.exception;

import lombok.Getter;

@Getter
public class WalletNotFoundException extends RuntimeException {

    final String message;
    final Integer code;

    public WalletNotFoundException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }
}
