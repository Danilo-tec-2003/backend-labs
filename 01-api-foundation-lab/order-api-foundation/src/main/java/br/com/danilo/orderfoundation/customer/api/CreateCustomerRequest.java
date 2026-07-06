package br.com.danilo.orderfoundation.customer.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 120, message = "Name must have at most 120 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email is invalid")
        @Size(max = 180, message = "Email must have at most 180 characters")
        String email,

        @NotBlank(message = "Document is required")
        @Size(max = 20, message = "Document must have at most 20 characters")
        String document
) {
}
