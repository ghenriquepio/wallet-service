package com.br.recargapay.walletservice.adapter.out.persistence;

import com.br.recargapay.walletservice.domain.model.Transaction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
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

@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEntity {

    @Id
    @Column("id")
    private UUID id;

    @NotNull
    @Column("wallet_id")
    private UUID walletId;

    @NotBlank
    @Column("type")
    private String type;

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

    public static TransactionEntity fromDomain(Transaction domain) {
        return TransactionEntity.builder()
                .id(domain.getId())
                .walletId(domain.getWalletId())
                .type(domain.getType())
                .amount(domain.getAmount())
                .description(domain.getDescription())
                .build();
    }

    public Transaction toDomain() {
        return Transaction.builder()
                .id(this.id)
                .walletId(this.walletId)
                .type(this.type)
                .amount(this.amount)
                .description(this.description)
                .build();
    }
}
