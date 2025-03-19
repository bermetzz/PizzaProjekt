package whz.pti.eva.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import whz.pti.eva.cart.domain.CartItemDTO;
import whz.pti.eva.customer.domain.CustomerDTO;
import whz.pti.eva.customer.service.CustomerService;
import whz.pti.eva.pizza.domain.PizzaDTO;
import whz.pti.eva.pizza.service.PizzaService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // The test profile to disable InitDatabase
public class PizzaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PizzaService pizzaService;

    @MockBean
    private CustomerService customerService;

    private List<PizzaDTO> mockPizzas;
    private CustomerDTO mockUserDTO;
    private List<CartItemDTO> mockCartItems;

    @BeforeEach
    void setUp() {
        // Mock pizzas
        mockPizzas = Arrays.asList(
                new PizzaDTO(1L, "Margherita", "image1.png", List.of("10.99", "12.99", "14.99")),
                new PizzaDTO(2L, "Pepperoni", "image2.png", List.of("11.99", "13.99", "15.99"))
        );

        // Mock user
        mockUserDTO = CustomerDTO.builder()
                .username("test_user")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .role("USER")
                .addresses("123 Main St, City, Country")
                .build();

        // Mock cart items
        mockCartItems = Arrays.asList(
                new CartItemDTO(1L, "Pizza Margherita", "Medium", BigDecimal.valueOf(10.99), 2, "margherita.png"),
                new CartItemDTO(2L, "Pizza Pepperoni", "Large", BigDecimal.valueOf(12.99), 1, "pepperoni.png")
        );

        // Mock behaviors
        Mockito.when(pizzaService.getAllPizzas()).thenReturn(mockPizzas);
        Mockito.when(customerService.getUserDTOByUsername(anyString())).thenReturn(mockUserDTO);
        Mockito.when(customerService.getCartItemsByUsername(anyString())).thenReturn(mockCartItems);
    }

    @Test
    @WithMockUser(username = "test_user")
    public void testShowPizzasAuthenticated() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("pizzas"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attribute("pizzas", mockPizzas))
                .andExpect(model().attribute("user", mockUserDTO))
                .andExpect(model().attribute("cartItems", mockCartItems));

        Mockito.verify(pizzaService, times(1)).getAllPizzas();
        Mockito.verify(customerService, times(1)).getUserDTOByUsername("test_user");
        Mockito.verify(customerService, times(3)).getCartItemsByUsername("test_user");
    }

    @Test
    public void testShowPizzasUnauthenticated() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("pizzas"))
                .andExpect(model().attributeDoesNotExist("user"))
                .andExpect(model().attributeDoesNotExist("cartItems"))
                .andExpect(model().attribute("pizzas", mockPizzas));

        Mockito.verify(pizzaService, times(1)).getAllPizzas();
        Mockito.verify(customerService, times(0)).getUserDTOByUsername(Mockito.anyString());
        Mockito.verify(customerService, times(0)).getCartItemsByUsername(Mockito.anyString());
    }
}
