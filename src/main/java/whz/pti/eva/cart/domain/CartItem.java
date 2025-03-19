package whz.pti.eva.cart.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import whz.pti.eva.pizza.domain.Pizza;
import whz.pti.eva.pizza.domain.Price;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    @ManyToOne
    private Pizza pizza;

    @ManyToOne
    private Price price;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
}