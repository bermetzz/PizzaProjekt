package whz.pti.eva.admin.service;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.CustomerRepository;
import whz.pti.eva.cart.domain.CartRepository;
import whz.pti.eva.customer.domain.DeliveryAddress;

import java.util.List;
import java.util.Optional;


@Service
@Data
@RequiredArgsConstructor
public class AdminService {

    @Autowired
    private final CustomerRepository customerRepository;

    @Autowired
    private final CartRepository cartRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Transactional
    public void updateCustomer(Long id, Customer updatedCustomer) {
        Customer customerDB = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        customerDB.setFirstName(updatedCustomer.getFirstName());
        customerDB.setLastName(updatedCustomer.getLastName());
        customerDB.setEmail(updatedCustomer.getEmail());
        customerDB.setRole(updatedCustomer.getRole());

        customerDB.getDeliveryAddresses().clear();
        for (DeliveryAddress address : updatedCustomer.getDeliveryAddresses()) {
            address.setCustomer(customerDB);
            customerDB.getDeliveryAddresses().add(address);
        }

        customerRepository.save(customerDB);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        cartRepository.deleteByCustomer(customer);

        customerRepository.delete(customer);
    }

}
