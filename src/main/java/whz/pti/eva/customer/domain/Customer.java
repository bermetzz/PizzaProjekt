package whz.pti.eva.customer.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import whz.pti.eva.common.domain.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class Customer extends BaseEntity {

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 50, message = "First name must be at most 50 characters long")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 50, message = "Last name must be at most 50 characters long")
    private String lastName;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email cannot be blank")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Username cannot be blank")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Column(nullable = false)
    private String password;

    @NotNull(message = "Role cannot be null")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Roles role=Roles.USER;

    @NotNull
    @Builder.Default
    private Boolean enabled=true;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer", orphanRemoval = true,  fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<DeliveryAddress> deliveryAddresses = new ArrayList<>();


    public Customer(String firstName, String lastName, String email, String username, String password, Roles role, List<DeliveryAddress> deliveryAddresses) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.deliveryAddresses = deliveryAddresses;
    }
}