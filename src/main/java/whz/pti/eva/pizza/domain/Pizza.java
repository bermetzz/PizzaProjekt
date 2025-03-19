package whz.pti.eva.pizza.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import whz.pti.eva.common.domain.BaseEntity;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Pizza extends BaseEntity {

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 255, message = "Name must be at most 255 characters long")
    private String name;

    @Column(name = "image_path", length = 1000)
    @Size(max = 1000, message = "Image path must be at most 1000 characters long")
    private String imagePath;

    @OneToMany(mappedBy = "pizza", cascade = CascadeType.ALL)
    @NotEmpty(message = "Prices cannot be empty")
    private List<Price> prices;
}