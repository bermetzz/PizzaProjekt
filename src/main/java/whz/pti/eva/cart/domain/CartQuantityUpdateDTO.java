package whz.pti.eva.cart.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartQuantityUpdateDTO {
    private Long pizzaId;
    private String size;
    private int delta;
}