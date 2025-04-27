package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.adapter.in.rest.dto.response.WalletBalanceResponse;
import com.br.recargapay.walletservice.application.port.in.ConsultWalletUseCase;
import com.br.recargapay.walletservice.application.port.in.RetrieveWalletBalanceUseCase;
import com.br.recargapay.walletservice.domain.exception.WalletNotFoundException;
import com.br.recargapay.walletservice.domain.model.Wallet;
import org.junit.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RetrieveWalletBalanceServiceTest {

    @Test
    public void test_find_balance_returns_wallet_balance_when_wallet_exists() {

        ConsultWalletUseCase consultWalletUseCase = Mockito.mock(ConsultWalletUseCase.class);
        RetrieveWalletBalanceUseCase retrieveWalletBalanceService = new RetrieveWalletBalanceService(consultWalletUseCase);

        UUID walletId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal balance = BigDecimal.valueOf(100.50);
    
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setOwnerId(ownerId);
        wallet.setBalance(balance);
    
        when(consultWalletUseCase.findById(walletId)).thenReturn(Mono.just(wallet));

        Mono<WalletBalanceResponse> result = retrieveWalletBalanceService.findBalanceByWalletId(walletId);

        StepVerifier.create(result)
            .assertNext(response -> {
                assertEquals(walletId, response.id());
                assertEquals(ownerId, response.ownerId());
                assertEquals(balance, response.balance());
            })
            .verifyComplete();
    
        verify(consultWalletUseCase).findById(walletId);
    }

    @Test
    public void test_find_balance_throws_exception_when_wallet_not_found() {

        ConsultWalletUseCase consultWalletUseCase = Mockito.mock(ConsultWalletUseCase.class);
        RetrieveWalletBalanceUseCase retrieveWalletBalanceService = new RetrieveWalletBalanceService(consultWalletUseCase);

        UUID walletId = UUID.randomUUID();
        when(consultWalletUseCase.findById(walletId)).thenReturn(Mono.empty());

        Mono<WalletBalanceResponse> result = retrieveWalletBalanceService.findBalanceByWalletId(walletId);

        StepVerifier.create(result)
            .expectErrorMatches(throwable -> {
                assertTrue(throwable instanceof WalletNotFoundException);
                WalletNotFoundException exception = (WalletNotFoundException) throwable;
                assertNotNull(exception.getMessage());
                assertNotNull(exception.getCode());
                return true;
            })
            .verify();
    
        verify(consultWalletUseCase).findById(walletId);
    }

    @Test
    public void test_find_balance_handles_null_wallet_id() {

        ConsultWalletUseCase consultWalletUseCase = Mockito.mock(ConsultWalletUseCase.class);
        RetrieveWalletBalanceUseCase retrieveWalletBalanceService = new RetrieveWalletBalanceService(consultWalletUseCase);

        UUID nullWalletId = null;
        when(consultWalletUseCase.findById(nullWalletId)).thenReturn(Mono.error(new IllegalArgumentException("Wallet ID cannot be null")));

        Mono<WalletBalanceResponse> result = retrieveWalletBalanceService.findBalanceByWalletId(nullWalletId);

        StepVerifier.create(result)
            .expectError(IllegalArgumentException.class)
            .verify();
    
        verify(consultWalletUseCase).findById(nullWalletId);
    }
}