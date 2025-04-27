package com.br.recargapay.walletservice.adapter.out.persistence;

import com.br.recargapay.walletservice.domain.model.Wallet;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("wallet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletEntity {

    @Id
    private UUID id;

    @NotNull
    @Column("owner_id")
    private UUID ownerId;

    @NotNull
    @PositiveOrZero
    @Column("balance")
    private BigDecimal balance;

    @NotNull
    @Column("status_id")
    private Integer statusId;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    public static WalletEntity fromDomain(Wallet wallet) {
        return WalletEntity.builder()
                .ownerId(wallet.getOwnerId())
                .balance(wallet.getBalance())
                .statusId(wallet.getStatusId())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }

    public Wallet toDomain() {
        return Wallet.builder()
                .id(this.id)
                .ownerId(this.ownerId)
                .balance(this.balance)
                .statusId(this.statusId)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}