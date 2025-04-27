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
public class Transaction {
    private UUID id;
    private UUID walletId;
    private String type;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;
}