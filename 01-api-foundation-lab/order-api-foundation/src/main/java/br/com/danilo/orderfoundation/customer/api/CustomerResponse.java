package br.com.danilo.orderfoundation.customer.api;

import br.com.danilo.orderfoundation.customer.domain.Customer;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
       UUID id,
       String name,
       String email,
       String document,
       String status,
       Instant createdAt
) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail().value(),
                customer.getDocument().value(),
                customer.getStatus().name(),
                customer.getCreatedAt()
        );
    }
}
