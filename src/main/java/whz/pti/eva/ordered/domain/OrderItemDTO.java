package whz.pti.eva.ordered.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    @NotBlank(message = "Pizza name cannot be blank")
    private String pizzaName;

    @Size(max = 1000, message = "Image path must be at most 1000 characters long")
    private String imagePath;

    @Positive(message = "Quantity must be greater than zero")
    private int quantity;

    @Positive(message = "Unit price must be positive")
    private double unitPrice;

    @Positive(message = "Total price must be positive")
    private double totalPrice;
}