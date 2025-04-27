package com.br.recargapay.walletservice.adapter.in.rest.dto.response;

import com.br.recargapay.walletservice.adapter.out.persistence.PersonEntity;
import com.br.recargapay.walletservice.domain.model.Person;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response object representing a person entity with identification and contact details.
 *
 * @param id             Unique identifier of the person
 * @param fullName       Full name of the person
 * @param documentNumber Document number (e.g., CPF)
 * @param email          Email address of the person
 * @param phoneNumber    Phone number of the person
 * @param createdAt      Timestamp when the record was created
 * @param updatedAt      Timestamp when the record was last updated
 */
@Schema(description = "Response body representing the person details")
public record PersonResponse(

        @Schema(description = "Unique identifier of the person", example = "c01b8504-1841-4c6e-b174-f41d509f2019")
        UUID id,

        @NotBlank(message = "Full name is required")
        @Size(max = 255, message = "Full name must be at most 255 characters")
        @Schema(description = "Full name of the person", example = "John Doe")
        String fullName,

        @NotBlank(message = "Document number is required")
        @Size(max = 50, message = "Document number must be at most 50 characters")
        @Schema(description = "Document number (e.g., CPF)", example = "12345678901")
        String documentNumber,

        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must be at most 255 characters")
        @Schema(description = "Email address of the person", example = "john.doe@example.com")
        String email,

        @Size(max = 20, message = "Phone number must be at most 20 characters")
        @Schema(description = "Phone number of the person", example = "11999999999")
        String phoneNumber,

        @Schema(description = "Creation timestamp", example = "2024-04-21T18:35:00")
        LocalDateTime createdAt,

        @Schema(description = "Last update timestamp", example = "2024-04-22T10:10:00")
        LocalDateTime updatedAt

) {
    /**
     * Converts a {@link Person} domain object into a {@link PersonResponse}.
     *
     * @param domain the person domain model
     * @return a {@link PersonResponse} containing the person's information
     */
    public static PersonResponse fromDomain(Person domain) {
        return new PersonResponse(
                domain.getId(),
                domain.getFullName(),
                domain.getDocumentNumber(),
                domain.getEmail(),
                domain.getPhoneNumber(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}