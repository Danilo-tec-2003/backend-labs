package br.com.danilo.orderfoundation.customer.application;

public record CreateCustomerCommand(
        String name,
        String email,
        String document
) {
}
