package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.adapter.in.rest.dto.request.TransferFundsRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.response.TransferResponse;
import com.br.recargapay.walletservice.application.port.in.TransferFundsUseCase;
import com.br.recargapay.walletservice.application.port.out.TransferRepository;
import com.br.recargapay.walletservice.application.port.out.WalletRepository;
import com.br.recargapay.walletservice.domain.exception.WalletNotFoundException;
import com.br.recargapay.walletservice.domain.model.Transfer;
import com.br.recargapay.walletservice.domain.model.Wallet;
import org.junit.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static com.br.recargapay.walletservice.domain.exception.ExceptionEnum.WALLET_NOT_FOUND;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransferFoundsServiceTest {

    @Test
    public void test_transfer_funds_successfully() {

        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);
        TransferRepository transferRepository = Mockito.mock(TransferRepository.class);
        TransferFundsUseCase transferFundsService = new TransferFoundsService(transferRepository, walletRepository);

        UUID fromWalletId = UUID.randomUUID();
        UUID toWalletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");
    
        Wallet fromWallet = Wallet.builder()
                .id(fromWalletId)
                .balance(new BigDecimal("200.00"))
                .build();
    
        Wallet toWallet = Wallet.builder()
                .id(toWalletId)
                .balance(new BigDecimal("50.00"))
                .build();
    
        Wallet updatedFromWallet = Wallet.builder()
                .id(fromWalletId)
                .balance(new BigDecimal("100.00"))
                .build();
    
        Wallet updatedToWallet = Wallet.builder()
                .id(toWalletId)
                .balance(new BigDecimal("150.00"))
                .build();
    
        Transfer transfer = Transfer.builder()
                .walletIdFrom(fromWalletId)
                .walletIdTo(toWalletId)
                .amount(amount)
                .description("Transfer between wallets")
                .build();
    
        TransferFundsRequest request = new TransferFundsRequest(toWalletId, amount);
    
        when(walletRepository.findById(fromWalletId)).thenReturn(Mono.just(fromWallet));
        when(walletRepository.findById(toWalletId)).thenReturn(Mono.just(toWallet));
        when(transferRepository.save(any(Transfer.class))).thenReturn(Mono.just(transfer));
        when(walletRepository.updateBalance(any(Wallet.class)))
                .thenReturn(Mono.just(updatedFromWallet))
                .thenReturn(Mono.just(updatedToWallet));
    
        TransferResponse response = transferFundsService.transferFunds(fromWalletId, request)
                .block();

        assertNotNull(response);
        assertEquals(fromWalletId, response.fromWalletId());
        assertEquals(toWalletId, response.toWalletId());
        assertEquals(amount, response.transferredAmount());
    
        verify(walletRepository).findById(fromWalletId);
        verify(walletRepository).findById(toWalletId);
        verify(transferRepository).save(any(Transfer.class));
        verify(walletRepository, times(2)).updateBalance(any(Wallet.class));
    }

    @Test
    public void test_transfer_funds_source_wallet_not_found() {

        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);
        TransferRepository transferRepository = Mockito.mock(TransferRepository.class);
        TransferFundsUseCase transferFundsService = new TransferFoundsService(transferRepository, walletRepository);

        UUID fromWalletId = UUID.randomUUID();
        UUID toWalletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");
    
        TransferFundsRequest request = new TransferFundsRequest(toWalletId, amount);
    
        when(walletRepository.findById(fromWalletId)).thenReturn(Mono.empty());
        when(walletRepository.findById(toWalletId)).thenReturn(Mono.just(Wallet.builder().id(toWalletId).build()));

        StepVerifier.create(transferFundsService.transferFunds(fromWalletId, request))
                .expectErrorMatches(throwable -> {
                    assertThat(throwable).isInstanceOf(WalletNotFoundException.class);
                    WalletNotFoundException exception = (WalletNotFoundException) throwable;
                    assertThat(exception.getMessage()).isEqualTo(WALLET_NOT_FOUND.getMessage());
                    assertThat(exception.getCode()).isEqualTo(WALLET_NOT_FOUND.getStatusCode());
                    return true;
                })
                .verify();
    
        verify(walletRepository).findById(fromWalletId);
        verify(walletRepository).findById(toWalletId);
        verify(transferRepository, never()).save(any(Transfer.class));
        verify(walletRepository, never()).updateBalance(any(Wallet.class));
    }

    @Test
    public void test_transfer_funds_destination_wallet_not_found() {

        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);
        TransferRepository transferRepository = Mockito.mock(TransferRepository.class);
        TransferFundsUseCase transferFundsService = new TransferFoundsService(transferRepository, walletRepository);

        UUID fromWalletId = UUID.randomUUID();
        UUID toWalletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");
    
        Wallet fromWallet = Wallet.builder()
                .id(fromWalletId)
                .balance(new BigDecimal("200.00"))
                .build();
    
        TransferFundsRequest request = new TransferFundsRequest(toWalletId, amount);
    
        when(walletRepository.findById(fromWalletId)).thenReturn(Mono.just(fromWallet));
        when(walletRepository.findById(toWalletId)).thenReturn(Mono.empty());

        StepVerifier.create(transferFundsService.transferFunds(fromWalletId, request))
                .expectErrorMatches(throwable -> {
                    assertThat(throwable).isInstanceOf(WalletNotFoundException.class);
                    WalletNotFoundException exception = (WalletNotFoundException) throwable;
                    assertThat(exception.getMessage()).isEqualTo(WALLET_NOT_FOUND.getMessage());
                    assertThat(exception.getCode()).isEqualTo(WALLET_NOT_FOUND.getStatusCode());
                    return true;
                })
                .verify();
    
        verify(walletRepository).findById(fromWalletId);
        verify(walletRepository).findById(toWalletId);
        verify(transferRepository, never()).save(any(Transfer.class));
        verify(walletRepository, never()).updateBalance(any(Wallet.class));
    }
}