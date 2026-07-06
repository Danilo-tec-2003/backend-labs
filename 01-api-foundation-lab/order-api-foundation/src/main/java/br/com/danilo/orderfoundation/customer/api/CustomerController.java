package br.com.danilo.orderfoundation.customer.api;

import br.com.danilo.orderfoundation.customer.application.CreateCustomerCommand;
import br.com.danilo.orderfoundation.customer.application.CreateCustomerUseCase;
import br.com.danilo.orderfoundation.customer.domain.Customer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;

    public CustomerController(CreateCustomerUseCase createCustomerUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody CreateCustomerRequest request) {
        CreateCustomerCommand command = new CreateCustomerCommand(
                request.name(),
                request.email(),
                request.document()
        );

        Customer customer = createCustomerUseCase.execute(command);
        return CustomerResponse.from(customer);
    }
}
