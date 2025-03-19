package whz.pti.eva.pizza.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PizzaDTO {
    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 255, message = "Name must be at most 255 characters long")
    private String name;

    @Size(max = 1000, message = "Image path must be at most 1000 characters long")
    private String imagePath;

    @NotEmpty(message = "Prices list cannot be empty")
    private List<@NotBlank(message = "Price value cannot be blank") String> prices;
}