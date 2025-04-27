package com.br.recargapay.walletservice.adapter.out.persistence;

import com.br.recargapay.walletservice.domain.model.Person;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Table("person")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PersonEntity {

    @Id
    private UUID id;

    @NotBlank
    @Size(max = 255)
    @Column("full_name")
    private String fullName;

    @NotBlank
    @Size(max = 50)
    @Column("document_number")
    private String documentNumber;

    @Email
    @Size(max = 255)
    @Column("email")
    private String email;

    @Size(max = 20)
    @Column("phone_number")
    private String phoneNumber;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    public static PersonEntity fromDomain(Person domain) {
        return PersonEntity.builder()
                .fullName(domain.getFullName())
                .documentNumber(domain.getDocumentNumber())
                .email(domain.getEmail())
                .phoneNumber(domain.getPhoneNumber())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public Person toDomain() {
        return Person.builder()
                .id(this.id)
                .fullName(this.fullName)
                .documentNumber(this.documentNumber)
                .email(this.email)
                .phoneNumber(this.phoneNumber)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
