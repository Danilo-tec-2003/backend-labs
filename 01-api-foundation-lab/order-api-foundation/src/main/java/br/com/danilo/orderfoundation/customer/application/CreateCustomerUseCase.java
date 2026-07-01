package br.com.danilo.orderfoundation.customer.application;

import br.com.danilo.orderfoundation.customer.domain.Customer;
import br.com.danilo.orderfoundation.customer.domain.DocumentNumber;
import br.com.danilo.orderfoundation.customer.domain.Email;

public class CreateCustomerUseCase {

    private final CustomerRepository customerRepository;

    public CreateCustomerUseCase (CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer execute(CreateCustomerCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("O comando de criação de cliente é obrigatório.");
        }

        Email email = Email.of(command.email());
        DocumentNumber document = DocumentNumber.of(command.document());

        if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("e-mail do cliente já existe.");
        }

        if (customerRepository.existsByDocument(document)) {
            throw new IllegalArgumentException("documento do cliente já existe.");
        }

        Customer customer = Customer.create(command.name(), email, document);
        return customerRepository.save(customer);

        }
    }
