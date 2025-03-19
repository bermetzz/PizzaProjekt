package whz.pti.eva.ordered.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    @NotNull(message = "Order ID cannot be null")
    private Long id;

    @NotBlank(message = "Date cannot be blank")
    private String formattedDate;

    @NotEmpty(message = "Order must contain at least one item")
    private List<@Valid OrderItemDTO> items;

    @Positive(message = "Total amount must be positive")
    private double totalAmount;
}