package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.application.port.out.WalletRepository;
import com.br.recargapay.walletservice.domain.model.Wallet;
import org.junit.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

public class ConsultWalletServiceTest {

    @Test
    public void test_find_by_id_returns_wallet_when_valid_uuid() {

        UUID walletId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal balance = BigDecimal.valueOf(100.0);
    
        Wallet expectedWallet = Wallet.builder()
            .id(walletId)
            .ownerId(ownerId)
            .balance(balance)
            .statusId(1)
            .build();
    
        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);
        Mockito.when(walletRepository.findById(walletId)).thenReturn(Mono.just(expectedWallet));
    
        ConsultWalletService consultWalletService = new ConsultWalletService(walletRepository);
    
        Mono<Wallet> result = consultWalletService.findById(walletId);
    
        StepVerifier.create(result)
            .expectNext(expectedWallet)
            .verifyComplete();
    
        Mockito.verify(walletRepository).findById(walletId);
    }

    @Test
    public void test_find_by_id_returns_empty_when_wallet_not_found() {

        UUID nonExistentWalletId = UUID.randomUUID();
    
        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);
        Mockito.when(walletRepository.findById(nonExistentWalletId)).thenReturn(Mono.empty());
    
        ConsultWalletService consultWalletService = new ConsultWalletService(walletRepository);
    
        Mono<Wallet> result = consultWalletService.findById(nonExistentWalletId);
    
        StepVerifier.create(result)
            .verifyComplete();
    
        Mockito.verify(walletRepository).findById(nonExistentWalletId);
    }

    @Test
    public void test_find_by_id_handles_null_id() {

        UUID nullId = null;
    
        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);
        Mockito.when(walletRepository.findById(nullId)).thenReturn(Mono.empty());
    
        ConsultWalletService consultWalletService = new ConsultWalletService(walletRepository);
    
        Mono<Wallet> result = consultWalletService.findById(nullId);
    
        StepVerifier.create(result)
            .verifyComplete();
    
        Mockito.verify(walletRepository).findById(nullId);
    }
}