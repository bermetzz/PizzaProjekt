package whz.pti.eva.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.CustomerRepository;
import whz.pti.eva.customer.domain.DeliveryAddress;
import whz.pti.eva.customer.domain.Roles;
import whz.pti.eva.customer.service.CustomerService;
import whz.pti.eva.pizza.domain.Pizza;
import whz.pti.eva.pizza.domain.PizzaRepository;
import whz.pti.eva.pizza.domain.Price;
import whz.pti.eva.pizza.domain.PizzaSize;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Configuration
@Profile("!test")
public class InitDatabase {
    @Bean
    CommandLineRunner init(PizzaRepository pizzaRepository, CustomerRepository customerRepository, CustomerService customerService) {
        return args -> {
            // Create and add pizzas if they don't already exist
            addPizzaIfNotExists(pizzaRepository, "Pesto Pizza", "https://images.aws.nestle.recipes/resized/227ef96019fb531bab6ccdb7f1109538_id72820_pestopizza01b_944_531.jpg",
                    List.of(
                            createPrice(PizzaSize.SMALL, BigDecimal.valueOf(400)),
                            createPrice(PizzaSize.MEDIUM, BigDecimal.valueOf(500)),
                            createPrice(PizzaSize.LARGE, BigDecimal.valueOf(600))
                    )
            );

            addPizzaIfNotExists(pizzaRepository, "Margherita", "https://www.justspices.de/media/recipe/p/i/pizza_gewuerz_pizza_margherita-2.jpg",
                    List.of(
                            createPrice(PizzaSize.SMALL, BigDecimal.valueOf(420)),
                            createPrice(PizzaSize.MEDIUM, BigDecimal.valueOf(520)),
                            createPrice(PizzaSize.LARGE, BigDecimal.valueOf(620))
                    )
            );

            addPizzaIfNotExists(pizzaRepository, "Pepperoni", "https://www.simplyrecipes.com/thmb/pjYMLcsKHkr8D8tYixmaFNxppPw=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/__opt__aboutcom__coeus__resources__content_migration__simply_recipes__uploads__2019__09__easy-pepperoni-pizza-lead-3-8f256746d649404baa36a44d271329bc.jpg",
                    List.of(
                            createPrice(PizzaSize.SMALL, BigDecimal.valueOf(450)),
                            createPrice(PizzaSize.MEDIUM, BigDecimal.valueOf(550)),
                            createPrice(PizzaSize.LARGE, BigDecimal.valueOf(650))
                    )
            );

            addPizzaIfNotExists(pizzaRepository, "Four Cheeses", "https://kitchenatics.com/wp-content/uploads/2020/09/Cheese-pizza-1.jpg",
                    List.of(
                            createPrice(PizzaSize.SMALL, BigDecimal.valueOf(480)),
                            createPrice(PizzaSize.MEDIUM, BigDecimal.valueOf(580)),
                            createPrice(PizzaSize.LARGE, BigDecimal.valueOf(680))
                    )
            );

            addPizzaIfNotExists(pizzaRepository, "Veggie Delight", "https://images.mrcook.app/recipe-image/018fe9d5-0b38-788c-aa8c-5ca42cd92007",
                    List.of(
                            createPrice(PizzaSize.SMALL, BigDecimal.valueOf(430)),
                            createPrice(PizzaSize.MEDIUM, BigDecimal.valueOf(530)),
                            createPrice(PizzaSize.LARGE, BigDecimal.valueOf(630))
                    )
            );

            addPizzaIfNotExists(pizzaRepository, "BBQ Chicken", "https://www.allrecipes.com/thmb/ee0daLeNNIUcrKbm5nxwFXheMDM=/0x512/filters:no_upscale():max_bytes(150000):strip_icc()/AR-24878-bbq-chicken-pizza-beauty-4x3-39cd80585ad04941914dca4bd82eae3d.jpg",
                    List.of(
                            createPrice(PizzaSize.SMALL, BigDecimal.valueOf(470)),
                            createPrice(PizzaSize.MEDIUM, BigDecimal.valueOf(570)),
                            createPrice(PizzaSize.LARGE, BigDecimal.valueOf(670))
                    )
            );

            addPizzaIfNotExists(pizzaRepository, "Hawaiian", "https://www.allrecipes.com/thmb/v1Xi2wtebK1sZwSJitdV4MGKl1c=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/hawaiian-pizza-ddmfs-3x2-132-450eff04ad924d9a9eae98ca44e3f988.jpg",
                    List.of(
                            createPrice(PizzaSize.SMALL, BigDecimal.valueOf(440)),
                            createPrice(PizzaSize.MEDIUM, BigDecimal.valueOf(540)),
                            createPrice(PizzaSize.LARGE, BigDecimal.valueOf(640))
                    )
            );

            addPizzaIfNotExists(pizzaRepository, "Meat Lover's", "https://www.thespruceeats.com/thmb/xuxwh4RIGcZMgaJE8u3SueM0SoA=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/aqIMG_4568fhor-0b89dc5c8c494ee9828ed29805791c5a.jpg",
                    List.of(
                            createPrice(PizzaSize.SMALL, BigDecimal.valueOf(490)),
                            createPrice(PizzaSize.MEDIUM, BigDecimal.valueOf(590)),
                            createPrice(PizzaSize.LARGE, BigDecimal.valueOf(690))
                    )
            );

            addPizzaIfNotExists(pizzaRepository, "Seafood Special", "https://www.dominos.jp/ManagedAssets/JP/product/1091/JP_1091_en_hero_13567.jpg?v1653473531",
                    List.of(
                            createPrice(PizzaSize.SMALL, BigDecimal.valueOf(500)),
                            createPrice(PizzaSize.MEDIUM, BigDecimal.valueOf(600)),
                            createPrice(PizzaSize.LARGE, BigDecimal.valueOf(700))
                    )
            );

            //create users
            if (customerRepository.findByUsername("bnutz").isEmpty()) {

                customerService.register(Customer.builder()
                        .username("bnutz")
                        .password("n1")
                        .role(Roles.USER)
                        .firstName("Ben")
                        .lastName("Nutz")
                        .email("bnutz@example.com")
                        .build());

                Customer benCustomer = customerRepository.findByUsername("bnutz")
                        .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

                DeliveryAddress benAddress = DeliveryAddress.builder()
                        .country("Deutschland")
                        .city("Cologne")
                        .street("Hohenzollernring 85")
                        .postalCode("50672")
                        .customer(benCustomer)
                        .build();

                benCustomer.getDeliveryAddresses().add(benAddress);

                customerRepository.save(benCustomer);

            }
            if (customerRepository.findByUsername("cnutz").isEmpty()) {

                Customer customer = Customer.builder()
                        .username("cnutz")
                        .password("n2")
                        .firstName("Chris")
                        .lastName("Nutz")
                        .email("cnutz@example.com")
                        .role(Roles.USER)
                        .build();

                customerService.register(customer);

                Customer customerDB = customerRepository.findByUsername("cnutz")
                        .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

// Create delivery addresses
                DeliveryAddress address1 = DeliveryAddress.builder()
                        .country("Deutschland")
                        .city("Zwickau")
                        .street("Innere Schneeberger Straße")
                        .postalCode("08056")
                        .customer(customerDB) // Link to the persisted customer
                        .build();

                DeliveryAddress address2 = DeliveryAddress.builder()
                        .country("Deutschland")
                        .city("Aachen")
                        .street("Turmstraße 176")
                        .postalCode("52064")
                        .customer(customerDB) // Link to the persisted customer
                        .build();

                DeliveryAddress address3 = DeliveryAddress.builder()
                        .country("Deutschland")
                        .city("Berlin")
                        .street("Alexanderplatz 1")
                        .postalCode("10178")
                        .customer(customerDB) // Link to the persisted customer
                        .build();

                DeliveryAddress address4 = DeliveryAddress.builder()
                        .country("Deutschland")
                        .city("Hamburg")
                        .street("Reeperbahn 42")
                        .postalCode("20359")
                        .customer(customerDB) // Link to the persisted customer
                        .build();

                DeliveryAddress address5 = DeliveryAddress.builder()
                        .country("Deutschland")
                        .city("Munich")
                        .street("Marienplatz 5")
                        .postalCode("80331")
                        .customer(customerDB) // Link to the persisted customer
                        .build();

// Add addresses to the customer
                customerDB.getDeliveryAddresses().add(address1);
                customerDB.getDeliveryAddresses().add(address2);
                customerDB.getDeliveryAddresses().add(address3);
                customerDB.getDeliveryAddresses().add(address4);
                customerDB.getDeliveryAddresses().add(address5);

// Save the customer with new addresses
                customerRepository.save(customerDB);


            }
            if (customerRepository.findByUsername("admin").isEmpty()) {
                customerService.register(Customer.builder()
                        .username("admin")
                        .password("a1")
                        .firstName("Admin")
                        .lastName("User")
                        .email("admin@example.com")
                        .role(Roles.ADMIN)
                        .build());
            }

            if (customerRepository.findByUsername("admin2").isEmpty()) {
                customerService.register(Customer.builder()
                        .username("admin2")
                        .password("a2")
                        .firstName("Admin2")
                        .lastName("User2")
                        .email("admin2@example.com")
                        .role(Roles.ADMIN)
                        .build());
            }
        };
    }

    private void addPizzaIfNotExists(PizzaRepository pizzaRepository, String name, String imagePath, List<Price> prices) {
        Optional<Pizza> existingPizza = pizzaRepository.findByName(name);
        if (existingPizza.isEmpty()) {
            Pizza pizza = new Pizza();
            pizza.setName(name);
            pizza.setPrices(prices);
            prices.forEach(price -> price.setPizza(pizza));
            pizza.setImagePath(imagePath);
            pizzaRepository.save(pizza);
            System.out.println("Added pizza: " + name);
        }
    }

    private Price createPrice(PizzaSize size, BigDecimal amount) {
        Price price = new Price();
        price.setSize(size);
        price.setAmount(amount);
        return price;
    }
}