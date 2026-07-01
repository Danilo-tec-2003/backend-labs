package br.com.danilo.orderfoundation.customer.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentNumberTest {

    @Test
    void shouldCreateNormalizedCpfDocumentNumber() {
        DocumentNumber document = DocumentNumber.of("123.456.789-01");

        assertEquals("12345678901", document.value());
    }

    @Test
    void shouldCreateNormalizedCnpjDocumentNumber() {
        DocumentNumber document = DocumentNumber.of("12.345.678/0001-90");

        assertEquals("12345678000190", document.value());
    }

    @Test
    void shouldCompareDocumentsByValue() {
        DocumentNumber document1 = DocumentNumber.of("123.456.789-01");
        DocumentNumber document2 = DocumentNumber.of("12345678901");

        assertEquals(document1, document2);
        assertEquals(document1.hashCode(), document2.hashCode());
    }

    @Test
    void shouldNotCreateBlankDocumentNumber() {
        assertThrows(IllegalArgumentException.class, () -> DocumentNumber.of(null));
        assertThrows(IllegalArgumentException.class, () -> DocumentNumber.of(""));
        assertThrows(IllegalArgumentException.class, () -> DocumentNumber.of("   "));
    }

    @Test
    void shouldNotCreateDocumentNumberWithInvalidLength() {
        assertThrows(IllegalArgumentException.class, () -> DocumentNumber.of("123"));
        assertThrows(IllegalArgumentException.class, () -> DocumentNumber.of("123456789012"));
    }
}
