package com.br.recargapay.walletservice.domain.exception.handler;

import com.br.recargapay.walletservice.adapter.in.rest.dto.response.ErrorResponse;
import com.br.recargapay.walletservice.domain.exception.InsufficientFundsException;
import com.br.recargapay.walletservice.domain.exception.PersonExistsException;
import com.br.recargapay.walletservice.domain.exception.WalletNotFoundException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class BusinessExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationErrors(WebExchangeBindException webExchangeBindException) {
        StringBuilder messageBuilder = new StringBuilder("Validation failed for fields: ");
        webExchangeBindException.getFieldErrors().forEach(error ->
                messageBuilder.append(String.format("[%s: %s] ", error.getField(), error.getDefaultMessage()))
        );

        ErrorResponse errorResponse = new ErrorResponse(
                messageBuilder.toString().trim(),
                HttpStatus.BAD_REQUEST.value()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
    }

    @ExceptionHandler(PersonExistsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handlePersonExistsException(PersonExistsException personExistsException) {
        ErrorResponse errorResponse = new ErrorResponse(personExistsException.getMessage(), personExistsException.getCode());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleWalletNotFoundException(WalletNotFoundException walletNotFoundException) {
        ErrorResponse errorResponse = new ErrorResponse(walletNotFoundException.getMessage(), walletNotFoundException.getCode());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
    }

    @ExceptionHandler(TypeMismatchException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleTypeMismatchException(TypeMismatchException serverWebInputException) {
        ErrorResponse error = new ErrorResponse("Invalid date format. Expected ISO 8601 like '2025-04-22T15:00:00'", HttpStatus.BAD_REQUEST.value());
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInsufficientFundsException(InsufficientFundsException insufficientFundsException) {
        ErrorResponse error = new ErrorResponse(insufficientFundsException.getMessage(), insufficientFundsException.getCode());
        return Mono.just(ResponseEntity.badRequest().body(error));
    }
}
