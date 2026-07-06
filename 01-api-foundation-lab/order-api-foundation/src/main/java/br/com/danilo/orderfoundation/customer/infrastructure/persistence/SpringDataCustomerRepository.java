package br.com.danilo.orderfoundation.customer.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface SpringDataCustomerRepository extends JpaRepository<CustomerJpaEntity, UUID> {

    boolean existsByEmail(String email);

    boolean existsByDocument(String document);
}
