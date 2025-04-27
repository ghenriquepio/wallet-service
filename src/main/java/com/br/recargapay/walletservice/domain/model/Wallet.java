package com.br.recargapay.walletservice.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

    private UUID id;
    private UUID ownerId;
    private BigDecimal balance;
    private Integer statusId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}