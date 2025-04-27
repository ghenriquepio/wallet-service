package com.br.recargapay.walletservice.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletStatus {

    private Integer id;
    private String name;
    private String description;
}