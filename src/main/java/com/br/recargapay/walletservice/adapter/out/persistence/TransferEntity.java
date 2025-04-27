package com.br.recargapay.walletservice.adapter.out.persistence;

import com.br.recargapay.walletservice.domain.model.Transfer;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("transfer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferEntity {

    @Id
    @Column("id")
    private UUID id;

    @NotNull
    @Column("wallet_id_from")
    private UUID walletIdFrom;

    @NotNull
    @Column("wallet_id_to")
    private UUID walletIdTo;

    @NotNull
    @DecimalMin("0.00")
    @Column("amount")
    private BigDecimal amount;

    @Column("description")
    private String description;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    public static TransferEntity fromDomain(Transfer domain) {
        return TransferEntity.builder()
                .id(domain.getId())
                .walletIdFrom(domain.getWalletIdFrom())
                .walletIdTo(domain.getWalletIdTo())
                .amount(domain.getAmount())
                .description(domain.getDescription())
                .build();
    }

    public Transfer toDomain() {
        return Transfer.builder()
                .id(this.id)
                .walletIdFrom(this.walletIdFrom)
                .walletIdTo(this.walletIdTo)
                .amount(this.amount)
                .description(this.description)
                .build();
    }
}
