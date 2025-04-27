package com.br.recargapay.walletservice.adapter.in.rest.dto.request;

import com.br.recargapay.walletservice.domain.model.Person;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO representing the data required to onboard a new user and create their associated wallet.
 *
 * <p>This request encapsulates personal information such as name, document number,
 * email, and phone number, all of which are required for the onboarding process.</p>
 *
 * @param fullName Full name of the user.
 * @param documentNumber Document number (e.g., CPF).
 * @param email Email address of the user.
 * @param phoneNumber Phone number of the user.
 */
@Schema(description = "Request body for onboarding a new user and creating their wallet.")
public record OnboardingRequest(

        @NotBlank(message = "Full name must not be blank")
        @Size(max = 255, message = "Full name must be at most 255 characters long")
        @Schema(description = "Full name of the person", example = "John Doe", required = true)
        String fullName,

        @NotBlank(message = "Document number must not be blank")
        @Size(max = 50, message = "Document number must be at most 50 characters long")
        @Schema(description = "Document number (e.g., CPF)", example = "12345678901", required = true)
        String documentNumber,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be a valid email address")
        @Size(max = 255, message = "Email must be at most 255 characters long")
        @Schema(description = "Email address of the person", example = "john.doe@example.com", required = true)
        String email,

        @NotBlank(message = "Phone number must not be blank")
        @Size(max = 20, message = "Phone number must be at most 20 characters long")
        @Schema(description = "Phone number of the person", example = "11999999999", required = true)
        String phoneNumber

) {
    /**
     * Converts this DTO into a {@link Person} domain object.
     *
     * @return a new {@link Person} instance populated with the provided onboarding data
     */
    public Person toDomain() {
        return Person.builder()
                .fullName(fullName)
                .documentNumber(documentNumber)
                .email(email)
                .phoneNumber(phoneNumber)
                .createdAt(LocalDateTime.now())
                .build();
    }
}