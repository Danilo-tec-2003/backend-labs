package br.com.danilo.orderfoundation.customer.application;

import br.com.danilo.orderfoundation.customer.domain.Customer;
import br.com.danilo.orderfoundation.customer.domain.DocumentNumber;
import br.com.danilo.orderfoundation.customer.domain.Email;
import org.springframework.stereotype.Service;

@Service
public class CreateCustomerUseCase {

    private final CustomerRepository customerRepository;

    public CreateCustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer execute(CreateCustomerCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Create customer command is required.");
        }

        Email email = Email.of(command.email());
        DocumentNumber document = DocumentNumber.of(command.document());

        if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Customer email already exists.");
        }

        if (customerRepository.existsByDocument(document)) {
            throw new IllegalArgumentException("Customer document already exists.");
        }

        Customer customer = Customer.create(command.name(), email, document);
        return customerRepository.save(customer);
    }
}
