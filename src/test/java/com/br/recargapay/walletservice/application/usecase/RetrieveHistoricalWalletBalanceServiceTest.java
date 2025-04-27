package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.application.port.out.TransactionRepository;
import com.br.recargapay.walletservice.application.port.out.TransferRepository;
import com.br.recargapay.walletservice.application.port.out.WalletRepository;
import com.br.recargapay.walletservice.domain.model.Wallet;
import org.junit.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import com.br.recargapay.walletservice.domain.exception.WalletNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.br.recargapay.walletservice.domain.exception.ExceptionEnum.WALLET_NOT_FOUND;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RetrieveHistoricalWalletBalanceServiceTest {

    @Test
    public void test_successfully_retrieves_historical_balance() {

        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);
        TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
        TransferRepository transferRepository = Mockito.mock(TransferRepository.class);
        RetrieveHistoricalWalletBalanceService service = new RetrieveHistoricalWalletBalanceService(transactionRepository, transferRepository, walletRepository);

        UUID walletId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        LocalDateTime referenceDate = LocalDateTime.now();

        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setOwnerId(ownerId);

        BigDecimal transactionBalance = new BigDecimal("100.00");
        BigDecimal inboundTransfers = new BigDecimal("50.00");
        BigDecimal outboundTransfers = new BigDecimal("30.00");
        BigDecimal expectedBalance = new BigDecimal("120.00");

        when(walletRepository.findById(walletId)).thenReturn(Mono.just(wallet));
        when(transactionRepository.getTransactionBalanceUntil(walletId, referenceDate)).thenReturn(Mono.just(transactionBalance));
        when(transferRepository.getInboundTransferSumUntil(walletId, referenceDate)).thenReturn(Mono.just(inboundTransfers));
        when(transferRepository.getOutboundTransferSumUntil(walletId, referenceDate)).thenReturn(Mono.just(outboundTransfers));

        StepVerifier.create(service.findHistoricalBalanceByWalletId(walletId, referenceDate))
            .assertNext(response -> {
                assertEquals(walletId, response.walletId());
                assertEquals(ownerId, response.ownerId());
                assertEquals(referenceDate, response.referenceDate());
                assertEquals(expectedBalance, response.balance());
            })
            .verifyComplete();

        verify(walletRepository).findById(walletId);
        verify(transactionRepository).getTransactionBalanceUntil(walletId, referenceDate);
        verify(transferRepository).getInboundTransferSumUntil(walletId, referenceDate);
        verify(transferRepository).getOutboundTransferSumUntil(walletId, referenceDate);
    }

    @Test
    public void test_throws_wallet_not_found_exception() {

        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);
        TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
        TransferRepository transferRepository = Mockito.mock(TransferRepository.class);
        RetrieveHistoricalWalletBalanceService service = new RetrieveHistoricalWalletBalanceService(transactionRepository, transferRepository, walletRepository);

        UUID nonExistentWalletId = UUID.randomUUID();
        LocalDateTime referenceDate = LocalDateTime.now();

        when(walletRepository.findById(nonExistentWalletId)).thenReturn(Mono.empty());

        StepVerifier.create(service.findHistoricalBalanceByWalletId(nonExistentWalletId, referenceDate))
            .expectErrorMatches(throwable -> {
                assertThat(throwable).isInstanceOf(WalletNotFoundException.class);
                WalletNotFoundException exception = (WalletNotFoundException) throwable;
                assertThat(exception.getMessage()).isEqualTo(WALLET_NOT_FOUND.getMessage());
                assertThat(exception.getCode()).isEqualTo(WALLET_NOT_FOUND.getStatusCode());
                return true;
            })
            .verify();

        verify(walletRepository).findById(nonExistentWalletId);
        verifyNoInteractions(transactionRepository);
        verifyNoInteractions(transferRepository);
    }

    @Test
    public void test_handles_empty_transaction_repository_results() {

        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);
        TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
        TransferRepository transferRepository = Mockito.mock(TransferRepository.class);
        RetrieveHistoricalWalletBalanceService service = new RetrieveHistoricalWalletBalanceService(transactionRepository, transferRepository, walletRepository);

        UUID walletId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        LocalDateTime referenceDate = LocalDateTime.now();

        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setOwnerId(ownerId);

        BigDecimal inboundTransfers = new BigDecimal("50.00");
        BigDecimal outboundTransfers = new BigDecimal("30.00");
        BigDecimal expectedBalance = new BigDecimal("20.00");

        when(walletRepository.findById(walletId)).thenReturn(Mono.just(wallet));
        when(transactionRepository.getTransactionBalanceUntil(walletId, referenceDate)).thenReturn(Mono.just(BigDecimal.ZERO));
        when(transferRepository.getInboundTransferSumUntil(walletId, referenceDate)).thenReturn(Mono.just(inboundTransfers));
        when(transferRepository.getOutboundTransferSumUntil(walletId, referenceDate)).thenReturn(Mono.just(outboundTransfers));

        StepVerifier.create(service.findHistoricalBalanceByWalletId(walletId, referenceDate))
            .assertNext(response -> {
                assertEquals(walletId, response.walletId());
                assertEquals(ownerId, response.ownerId());
                assertEquals(referenceDate, response.referenceDate());
                assertEquals(expectedBalance, response.balance());
            })
            .verifyComplete();

        verify(walletRepository).findById(walletId);
        verify(transactionRepository).getTransactionBalanceUntil(walletId, referenceDate);
        verify(transferRepository).getInboundTransferSumUntil(walletId, referenceDate);
        verify(transferRepository).getOutboundTransferSumUntil(walletId, referenceDate);
    }
}