package br.com.danilo.orderfoundation.customer.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerTest {

    @Test
    void shouldCreateCustomerWithPendingStatus() {
        Email email = Email.of("danilo@gmail.com");
        DocumentNumber document = DocumentNumber.of("123.456.789-01");

        Customer customer = Customer.create(" Danilo Mendes ", email, document);

        assertNotNull(customer.getId());
        assertEquals("Danilo Mendes", customer.getName());
        assertEquals(email, customer.getEmail());
        assertEquals(document, customer.getDocument());
        assertEquals(CustomerStatus.PENDING, customer.getStatus());
        assertNotNull(customer.getCreatedAt());
    }

    @Test
    void shouldNotCreateCustomerWithoutName() {
        Email email = Email.of("danilo@gmail.com");
        DocumentNumber document = DocumentNumber.of("123.456.789-01");

        assertThrows(IllegalArgumentException.class, () -> Customer.create(null, email, document));
        assertThrows(IllegalArgumentException.class, () -> Customer.create("", email, document));
        assertThrows(IllegalArgumentException.class, () -> Customer.create("   ", email, document));
    }

    @Test
    void shouldNotCreateCustomerWithoutEmail() {
        DocumentNumber document = DocumentNumber.of("123.456.789-01");

        assertThrows(IllegalArgumentException.class, () -> Customer.create("Danilo Mendes", null, document));
    }

    @Test
    void shouldNotCreateCustomerWithoutDocument() {
        Email email = Email.of("danilo@gmail.com");

        assertThrows(IllegalArgumentException.class, () -> Customer.create("Danilo Mendes", email, null));
    }
}
