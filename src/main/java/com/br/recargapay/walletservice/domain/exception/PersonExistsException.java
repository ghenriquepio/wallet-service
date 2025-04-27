package com.br.recargapay.walletservice.domain.exception;

import lombok.Getter;

@Getter
public class PersonExistsException extends RuntimeException {

    final String message;
    final Integer code;

    public PersonExistsException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }
}