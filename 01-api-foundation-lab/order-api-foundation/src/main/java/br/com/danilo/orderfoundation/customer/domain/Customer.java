package br.com.danilo.orderfoundation.customer.domain;

import java.time.Instant;
import java.util.UUID;

public class Customer {

    private final UUID id;
    private final String name;
    private final Email email;
    private final DocumentNumber document;
    private final CustomerStatus status;
    private final Instant createdAt;

    private Customer(
            UUID id,
            String name,
            Email email,
            DocumentNumber document,
            CustomerStatus status,
            Instant createdAt
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.document = document;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Customer create(String name, Email email, DocumentNumber document) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        if (email == null) {
            throw new IllegalArgumentException("Customer email is required.");
        }
        if (document == null) {
            throw new IllegalArgumentException("Customer document is required.");
        }

        return new Customer(
                UUID.randomUUID(),
                name.trim(),
                email,
                document,
                CustomerStatus.PENDING,
                Instant.now()
        );
    }

    public static Customer restore(
            UUID id,
            String name,
            Email email,
            DocumentNumber document,
            CustomerStatus status,
            Instant createdAt
    ) {
        if (id == null) {
            throw new IllegalArgumentException("Customer id is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (email == null) {
            throw new IllegalArgumentException("Customer email is required");
        }
        if (document == null) {
            throw new IllegalArgumentException("Customer document is required");
        }
        if (status == null) {
            throw new IllegalArgumentException("Customer status is required");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Customer creation date is required");
        }
        return new Customer(
                id,
                name.trim(),
                email,
                document,
                status,
                createdAt
        );
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public DocumentNumber getDocument() {
        return document;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
