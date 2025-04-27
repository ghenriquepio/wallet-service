package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.adapter.in.rest.dto.request.WithdrawFundsRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.response.WalletResponse;
import com.br.recargapay.walletservice.application.port.in.WithdrawFundsUseCase;
import com.br.recargapay.walletservice.application.port.out.TransactionRepository;
import com.br.recargapay.walletservice.application.port.out.WalletRepository;
import com.br.recargapay.walletservice.domain.exception.InsufficientFundsException;
import com.br.recargapay.walletservice.domain.exception.WalletNotFoundException;
import com.br.recargapay.walletservice.domain.model.Transaction;
import com.br.recargapay.walletservice.domain.model.Wallet;
import org.junit.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.br.recargapay.walletservice.domain.exception.ExceptionEnum.INSUFFICIENT_FUNDS;
import static com.br.recargapay.walletservice.domain.exception.ExceptionEnum.WALLET_NOT_FOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WithdrawFundsServiceTest {

    @Test
    public void test_withdraw_funds_successfully() {

        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);
        TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
        WithdrawFundsUseCase withdrawFundsService = new WithdrawFundsService(walletRepository, transactionRepository);

        UUID walletId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal initialBalance = new BigDecimal("100.00");
        BigDecimal withdrawAmount = new BigDecimal("50.00");

        Integer one = 1;
    
        Wallet wallet = Wallet.builder()
                .id(walletId)
                .ownerId(ownerId)
                .balance(initialBalance)
                .statusId(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    
        WithdrawFundsRequest request = new WithdrawFundsRequest(withdrawAmount);
    
        Transaction transaction = request.toDomain(walletId);
    
        Wallet updatedWallet = Wallet.builder()
                .id(walletId)
                .ownerId(ownerId)
                .balance(initialBalance.subtract(withdrawAmount))
                .statusId(1)
                .createdAt(wallet.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
    
        when(walletRepository.findById(walletId)).thenReturn(Mono.just(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(transaction));
        when(walletRepository.updateBalance(any(Wallet.class))).thenReturn(Mono.just(updatedWallet));
    
        Mono<WalletResponse> result = withdrawFundsService.withdraw(walletId, request);
    
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(walletId, response.id());
                    assertEquals(ownerId, response.ownerId());
                    assertEquals(new BigDecimal("50.00"), response.balance());
                    assertEquals(one, response.statusId());
                })
                .verifyComplete();
    
        verify(walletRepository).findById(walletId);
        verify(transactionRepository).save(any(Transaction.class));
        verify(walletRepository).updateBalance(any(Wallet.class));
    }

    @Test
    public void test_withdraw_from_nonexistent_wallet() {

        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);
        TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
        WithdrawFundsUseCase withdrawFundsService = new WithdrawFundsService(walletRepository, transactionRepository);

        UUID nonExistentWalletId = UUID.randomUUID();
        BigDecimal withdrawAmount = new BigDecimal("50.00");
        WithdrawFundsRequest request = new WithdrawFundsRequest(withdrawAmount);
    
        when(walletRepository.findById(nonExistentWalletId)).thenReturn(Mono.empty());
    
        Mono<WalletResponse> result = withdrawFundsService.withdraw(nonExistentWalletId, request);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> {
                    assertInstanceOf(WalletNotFoundException.class, throwable);
                    WalletNotFoundException exception = (WalletNotFoundException) throwable;
                    assertEquals(WALLET_NOT_FOUND.getMessage(), exception.getMessage());
                    assertEquals(WALLET_NOT_FOUND.getStatusCode(), exception.getCode());
                    return true;
                })
                .verify();
    
        verify(walletRepository).findById(nonExistentWalletId);
        verifyNoInteractions(transactionRepository);
        verify(walletRepository, never()).updateBalance(any(Wallet.class));
    }

    @Test
    public void test_withdraw_more_than_available_balance() {

        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);
        TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
        WithdrawFundsUseCase withdrawFundsService = new WithdrawFundsService(walletRepository, transactionRepository);

        UUID walletId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal initialBalance = new BigDecimal("50.00");
        BigDecimal withdrawAmount = new BigDecimal("100.00");

        Wallet wallet = Wallet.builder()
                .id(walletId)
                .ownerId(ownerId)
                .balance(initialBalance)
                .statusId(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        WithdrawFundsRequest request = new WithdrawFundsRequest(withdrawAmount);

        when(walletRepository.findById(walletId)).thenReturn(Mono.just(wallet));
        when(transactionRepository.save(any())).thenReturn(Mono.just(mock(Transaction.class)));

        Mono<WalletResponse> result = withdrawFundsService.withdraw(walletId, request);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> {
                    assertInstanceOf(InsufficientFundsException.class, throwable);
                    InsufficientFundsException exception = (InsufficientFundsException) throwable;
                    assertEquals(INSUFFICIENT_FUNDS.getMessage(), exception.getMessage());
                    assertEquals(INSUFFICIENT_FUNDS.getStatusCode(), exception.getCode());
                    return true;
                })
                .verify();

        verify(walletRepository).findById(walletId);
        verify(walletRepository, never()).updateBalance(any(Wallet.class));
    }
}