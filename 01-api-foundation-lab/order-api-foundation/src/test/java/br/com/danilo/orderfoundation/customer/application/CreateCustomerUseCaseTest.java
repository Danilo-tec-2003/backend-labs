package br.com.danilo.orderfoundation.customer.application;

import br.com.danilo.orderfoundation.customer.domain.Customer;
import br.com.danilo.orderfoundation.customer.domain.CustomerStatus;
import br.com.danilo.orderfoundation.customer.domain.DocumentNumber;
import br.com.danilo.orderfoundation.customer.domain.Email;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateCustomerUseCaseTest {

    @Test
    void shouldCreateCustomer() {
        InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        CreateCustomerUseCase useCase = new CreateCustomerUseCase(repository);
        CreateCustomerCommand command = new CreateCustomerCommand(
                "Danilo Mendes",
                "danilo@gmail.com",
                "123.456.789-01"
        );

        Customer customer = useCase.execute(command);

        assertNotNull(customer.getId());
        assertEquals("Danilo Mendes", customer.getName());
        assertEquals(Email.of("danilo@gmail.com"), customer.getEmail());
        assertEquals(DocumentNumber.of("12345678901"), customer.getDocument());
        assertEquals(CustomerStatus.PENDING, customer.getStatus());
        assertEquals(1, repository.count());
    }

    @Test
    void shoshouldNotCreateCustomerWhenCommandIsNull() {
        InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        CreateCustomerUseCase useCase = new CreateCustomerUseCase(repository);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(null));
        assertEquals(0, repository.count());
    }

    @Test
    void shouldNotCreateCustomerWhenEmailAlreadyExists() {
        InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        repository.save(Customer.create(
                "Existing Customer",
                Email.of("danilo@gmail.com"),
                DocumentNumber.of("123.456.789-01")
        ));
        CreateCustomerUseCase useCase = new CreateCustomerUseCase(repository);
        CreateCustomerCommand command = new CreateCustomerCommand(
                "Danilo Mendes",
                "DANILO@gmail.com",
                "987.654.321-00"
        );

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
        assertEquals(1, repository.count());
    }

    @Test
    void shouldNotCreateCustomerWhenDocumentAlreadyExists() {
        InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        repository.save(Customer.create(
                "Existing Customer",
                Email.of("existing@gmail.com"),
                DocumentNumber.of("123.456.789-01")
        ));
        CreateCustomerUseCase useCase = new CreateCustomerUseCase(repository);
        CreateCustomerCommand command = new CreateCustomerCommand(
                "Danilo Mendes",
                "danilo@gmail.com",
                "12345678901"
        );

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
        assertEquals(1, repository.count());
    }

    private static class InMemoryCustomerRepository implements CustomerRepository {

        private final List<Customer> customers = new ArrayList<>();

        @Override
        public boolean existsByEmail(Email email) {
            return customers.stream()
                    .anyMatch(customer -> customer.getEmail().equals(email));
        }

        @Override
        public boolean existsByDocument(DocumentNumber document) {
            return customers.stream()
                    .anyMatch(customer -> customer.getDocument().equals(document));
        }

        @Override
        public Customer save(Customer customer) {
            customers.add(customer);
            return customer;
        }

        int count() {
            return customers.size();
        }
    }
}
