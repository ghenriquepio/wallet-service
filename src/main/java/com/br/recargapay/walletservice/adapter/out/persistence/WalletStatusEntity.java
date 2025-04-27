package com.br.recargapay.walletservice.adapter.out.persistence;

import com.br.recargapay.walletservice.domain.model.WalletStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "wallet_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletStatusEntity {

    @Id
    private Integer id;

    @NotBlank
    @Size(max = 50)
    @Column("name")
    private String name;

    @Size(max = 255)
    @Column("description")
    private String description;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    public static WalletStatusEntity fromDomain(WalletStatus domain) {
        return WalletStatusEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .build();
    }

    public WalletStatus toDomain() {
        return WalletStatus.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .build();
    }
}