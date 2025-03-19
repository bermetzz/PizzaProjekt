package whz.pti.eva.ordered.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import whz.pti.eva.common.domain.BaseEntity;
import whz.pti.eva.customer.domain.Customer;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Ordered extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Customer cannot be null")
    private Customer customer;

    @OneToMany(mappedBy = "ordered", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderedItem> items;
}