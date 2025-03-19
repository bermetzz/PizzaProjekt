package whz.pti.eva.ordered.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import whz.pti.eva.customer.domain.Customer;

import java.util.List;

@Repository
public interface OrderedRepository extends JpaRepository<Ordered, Long> {
    List<Ordered> findByCustomerOrderByCreatedAtDesc(Customer customer);
}