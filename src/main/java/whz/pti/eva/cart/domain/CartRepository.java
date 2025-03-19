package whz.pti.eva.cart.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import whz.pti.eva.cart.domain.Cart;
import whz.pti.eva.customer.domain.Customer;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCustomerId(Long customerId);

    Optional<Cart> findByCustomer(Customer customer);

    void deleteByCustomer(Customer customer);
}
