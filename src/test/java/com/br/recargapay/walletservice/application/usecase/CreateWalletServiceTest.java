package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.application.port.out.WalletRepository;
import com.br.recargapay.walletservice.domain.model.Wallet;
import org.junit.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateWalletServiceTest {

    @Test
    public void test_create_wallet_with_valid_owner_id() {

        UUID ownerId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
    
        Wallet expectedWallet = Wallet.builder()
                .id(walletId)
                .ownerId(ownerId)
                .statusId(1)
                .createdAt(LocalDateTime.now())
                .build();
    
        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);
        Mockito.when(walletRepository.save(Mockito.any(Wallet.class)))
                .thenReturn(Mono.just(expectedWallet));
    
        CreateWalletService createWalletService = new CreateWalletService(walletRepository);
    
        Mono<Wallet> result = createWalletService.create(ownerId);
    
        StepVerifier.create(result)
                .expectNextMatches(wallet -> wallet.getOwnerId().equals(ownerId))
                .verifyComplete();
    
        Mockito.verify(walletRepository).save(Mockito.any(Wallet.class));
    }

    @Test
    public void test_create_wallet_when_repository_save_fails() {

        UUID ownerId = UUID.randomUUID();
        RuntimeException expectedException = new RuntimeException("Database connection error");
    
        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);
        Mockito.when(walletRepository.save(Mockito.any(Wallet.class)))
                .thenReturn(Mono.error(expectedException));
    
        CreateWalletService createWalletService = new CreateWalletService(walletRepository);
    
        StepVerifier.create(createWalletService.create(ownerId))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().equals("Database connection error"))
                .verify();
    
        Mockito.verify(walletRepository).save(Mockito.any(Wallet.class));
    }
}