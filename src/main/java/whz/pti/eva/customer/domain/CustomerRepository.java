package whz.pti.eva.customer.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import whz.pti.eva.customer.domain.Customer;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUsername(String username);
}
