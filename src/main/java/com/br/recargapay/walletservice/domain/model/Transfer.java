package com.br.recargapay.walletservice.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {
    private UUID id;
    private UUID walletIdFrom;
    private UUID walletIdTo;
    private BigDecimal amount;
    private String description;
}