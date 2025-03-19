package whz.pti.eva.cart.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CartItemDTO {
    private Long pizzaId;
    private String name;
    private String size;
    private BigDecimal price;
    private int quantity;
    private String image;
}