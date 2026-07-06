package br.com.danilo.orderfoundation.customer.infrastructure.persistence;

import br.com.danilo.orderfoundation.customer.application.CustomerRepository;
import br.com.danilo.orderfoundation.customer.domain.Customer;
import br.com.danilo.orderfoundation.customer.domain.CustomerStatus;
import br.com.danilo.orderfoundation.customer.domain.DocumentNumber;
import br.com.danilo.orderfoundation.customer.domain.Email;
import org.springframework.stereotype.Repository;

@Repository
public class JpaCustomerRepository implements CustomerRepository {

    private final SpringDataCustomerRepository springDataCustomerRepository;

    public JpaCustomerRepository(SpringDataCustomerRepository springDataCustomerRepository) {
        this.springDataCustomerRepository = springDataCustomerRepository;
    }

    @Override
    public boolean existsByEmail(Email email) {
        return springDataCustomerRepository.existsByEmail(email.value());
    }

    @Override
    public boolean existsByDocument(DocumentNumber document) {
        return springDataCustomerRepository.existsByDocument(document.value());
    }

    @Override
    public Customer save(Customer customer) {
        CustomerJpaEntity entity = toEntity(customer);
        CustomerJpaEntity savedEntity = springDataCustomerRepository.save(entity);

        return toDomain(savedEntity);
    }

    private CustomerJpaEntity toEntity(Customer customer) {
        return new CustomerJpaEntity(
                customer.getId(),
                customer.getName(),
                customer.getEmail().value(),
                customer.getDocument().value(),
                customer.getStatus().name(),
                customer.getCreatedAt()
        );
    }

    private Customer toDomain(CustomerJpaEntity entity) {
        return Customer.restore(
                entity.getId(),
                entity.getName(),
                Email.of(entity.getEmail()),
                DocumentNumber.of(entity.getDocument()),
                CustomerStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt()
        );
    }
}
