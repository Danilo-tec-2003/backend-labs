package br.com.danilo.orderfoundation.customer.application;

import br.com.danilo.orderfoundation.customer.domain.Customer;
import br.com.danilo.orderfoundation.customer.domain.DocumentNumber;
import br.com.danilo.orderfoundation.customer.domain.Email;

//Responsabilidade: ser a porta de persistência. A aplicação conhece a interface, não JPA diretamente.
public interface CustomerRepository {

    boolean existsByEmail(Email email);

    boolean existsByDocument(DocumentNumber document);

    Customer save(Customer customer);
}
