package whz.pti.eva.ordered.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import whz.pti.eva.common.domain.BaseEntity;
import whz.pti.eva.pizza.domain.Pizza;
import whz.pti.eva.pizza.domain.Price;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderedItem extends BaseEntity {

    @Positive(message = "Quantity must be greater than zero")
    private int quantity;

    @ManyToOne
    @NotNull(message = "Pizza cannot be null")
    private Pizza pizza;

    @ManyToOne
    @NotNull(message = "Price cannot be null")
    private Price price;

    @ManyToOne
    @NotNull(message = "Order cannot be null")
    private Ordered ordered;
}