package br.com.danilo.orderfoundation.customer.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void deveCriarEmailValidoNormalizado() {
        Email email = Email.of(" Danilo@GMAIL.com ");

        assertEquals("danilo@gmail.com", email.value());
    }

    @Test
    void deveCompararEmailsPeloValor() {
        Email email1 = Email.of(" Danilo@GMAIL.com ");
        Email email2 = Email.of("danilo@gmail.com");

        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    void naoDeveCriarEmailNuloOuVazio() {
        assertThrows(IllegalArgumentException.class, () -> Email.of(null));
        assertThrows(IllegalArgumentException.class, () -> Email.of(""));
        assertThrows(IllegalArgumentException.class, () -> Email.of("   "));
    }

    @Test
    void naoDeveCriarEmailInvalido() {
        assertThrows(IllegalArgumentException.class, () -> Email.of("danilo"));
        assertThrows(IllegalArgumentException.class, () -> Email.of("danilo@gmail"));
    }

    @Test
    void deveRetornarEmailNoToString() {
        Email email = Email.of("danilo@gmail.com");

        assertEquals("danilo@gmail.com", email.toString());
    }
}