package whz.pti.eva.pizza.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    Optional<Price> findByPizzaAndSize(Pizza pizza, PizzaSize size);
}
