package br.com.danilo.orderfoundation.customer.domain;

import java.util.Objects;

public class DocumentNumber {

    private final String value;

    private DocumentNumber(String value) {
        this.value = value;
    }

    public static DocumentNumber of(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new IllegalArgumentException("O número do documento é obrigatório.");
        }

        String normalized = rawValue.replaceAll("\\D", "");
        if (normalized.length() != 11 && normalized.length() != 14) {
            throw new IllegalArgumentException("O número do documento deve ter 11 ou 14 dígitos.");
        }

        return new DocumentNumber(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DocumentNumber that = (DocumentNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
