package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.adapter.in.rest.dto.request.DepositFundsRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.response.WalletResponse;
import com.br.recargapay.walletservice.application.port.in.DepositFundsUseCase;
import com.br.recargapay.walletservice.application.port.out.TransactionRepository;
import com.br.recargapay.walletservice.application.port.out.WalletRepository;
import com.br.recargapay.walletservice.domain.exception.WalletNotFoundException;
import com.br.recargapay.walletservice.domain.model.Transaction;
import com.br.recargapay.walletservice.domain.model.Wallet;
import org.junit.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static com.br.recargapay.walletservice.domain.exception.ExceptionEnum.WALLET_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DepositFundsServiceTest {

    @Test
    public void test_deposit_funds_successfully() {
        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);
        TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
        DepositFundsUseCase depositFundsService = new DepositFundsService(walletRepository, transactionRepository);

        UUID walletId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal initialBalance = new BigDecimal("100.00");
        BigDecimal depositAmount = new BigDecimal("50.00");
        BigDecimal expectedBalance = new BigDecimal("150.00");

        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setOwnerId(ownerId);
        wallet.setBalance(initialBalance);
        wallet.setStatusId(1);

        Wallet updatedWallet = new Wallet();
        updatedWallet.setId(walletId);
        updatedWallet.setOwnerId(ownerId);
        updatedWallet.setBalance(expectedBalance);
        updatedWallet.setStatusId(1);

        Transaction transaction = Transaction.builder()
            .walletId(walletId)
            .type("DEPOSIT")
            .amount(depositAmount)
            .description("Deposit")
            .build();

        DepositFundsRequest request = new DepositFundsRequest(depositAmount);

        when(walletRepository.findById(walletId)).thenReturn(Mono.just(wallet));
        when(walletRepository.updateBalance(any(Wallet.class))).thenReturn(Mono.just(updatedWallet));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(transaction));

        Mono<WalletResponse> result = depositFundsService.deposit(walletId, request);

        StepVerifier.create(result)
            .assertNext(response -> {
                assertEquals(walletId, response.id());
                assertEquals(ownerId, response.ownerId());
                assertEquals(expectedBalance, response.balance());
                assertEquals(1, response.statusId());
            })
            .verifyComplete();

        verify(walletRepository).findById(walletId);
        verify(walletRepository).updateBalance(any(Wallet.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    // Handle wallet not found scenario
    @Test
    public void test_deposit_funds_wallet_not_found() {
        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);
        TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
        DepositFundsService depositFundsService = new DepositFundsService(walletRepository, transactionRepository);

        UUID walletId = UUID.randomUUID();
        BigDecimal depositAmount = new BigDecimal("50.00");
        DepositFundsRequest request = new DepositFundsRequest(depositAmount);

        when(walletRepository.findById(walletId)).thenReturn(Mono.empty());

        Mono<WalletResponse> result = depositFundsService.deposit(walletId, request);

        StepVerifier.create(result)
            .expectErrorMatches(throwable ->
                throwable instanceof WalletNotFoundException &&
                ((WalletNotFoundException) throwable).getCode().equals(WALLET_NOT_FOUND.getStatusCode()))
            .verify();

        verify(walletRepository).findById(walletId);
        verify(walletRepository, Mockito.never()).updateBalance(any(Wallet.class));
        verify(transactionRepository, Mockito.never()).save(any(Transaction.class));
    }

    @Test
    public void test_deposit_large_amount() {
        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);
        TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
        DepositFundsService depositFundsService = new DepositFundsService(walletRepository, transactionRepository);

        UUID walletId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal initialBalance = new BigDecimal("1000.00");
        BigDecimal largeDepositAmount = new BigDecimal("9999999999.99");
        BigDecimal expectedBalance = initialBalance.add(largeDepositAmount);

        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setOwnerId(ownerId);
        wallet.setBalance(initialBalance);
        wallet.setStatusId(1);

        Wallet updatedWallet = new Wallet();
        updatedWallet.setId(walletId);
        updatedWallet.setOwnerId(ownerId);
        updatedWallet.setBalance(expectedBalance);
        updatedWallet.setStatusId(1);

        Transaction transaction = Transaction.builder()
            .walletId(walletId)
            .type("DEPOSIT")
            .amount(largeDepositAmount)
            .description("Deposit")
            .build();

        DepositFundsRequest request = new DepositFundsRequest(largeDepositAmount);

        when(walletRepository.findById(walletId)).thenReturn(Mono.just(wallet));
        when(walletRepository.updateBalance(any(Wallet.class))).thenReturn(Mono.just(updatedWallet));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(transaction));

        Mono<WalletResponse> result = depositFundsService.deposit(walletId, request);

        StepVerifier.create(result)
            .assertNext(response -> {
                assertEquals(walletId, response.id());
                assertEquals(ownerId, response.ownerId());
                assertEquals(expectedBalance, response.balance());
                assertEquals(1, response.statusId());
            })
            .verifyComplete();

        verify(walletRepository).findById(walletId);
        verify(walletRepository).updateBalance(argThat(w ->
            w.getBalance().compareTo(expectedBalance) == 0));
        verify(transactionRepository).save(argThat(t ->
            t.getAmount().compareTo(largeDepositAmount) == 0));
    }
}