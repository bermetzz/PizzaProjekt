package whz.pti.eva.cart.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import whz.pti.eva.cart.domain.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

}
