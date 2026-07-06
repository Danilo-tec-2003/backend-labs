package br.com.danilo.orderfoundation.customer.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
class CustomerJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 180)
    private String email;

    @Column(name = "document", nullable = false, unique = true, length = 14)
    private String document;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CustomerJpaEntity() {
    }

    CustomerJpaEntity(
            UUID id,
            String name,
            String email,
            String document,
            String status,
            Instant createdAt
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.document = document;
        this.status = status;
        this.createdAt = createdAt;
    }

    UUID getId() {
        return id;
    }

    String getName() {
        return name;
    }

    String getEmail() {
        return email;
    }

    String getDocument() {
        return document;
    }

    String getStatus() {
        return status;
    }

    Instant getCreatedAt() {
        return createdAt;
    }
}
