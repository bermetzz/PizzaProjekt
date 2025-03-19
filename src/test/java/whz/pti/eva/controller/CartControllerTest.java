package whz.pti.eva.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import whz.pti.eva.cart.domain.CartItemDTO;
import whz.pti.eva.cart.service.CartService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @BeforeEach
    public void setUp() {
        Authentication mockAuthentication = Mockito.mock(Authentication.class);
        Mockito.when(mockAuthentication.getName()).thenReturn("test_user");

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(mockAuthentication);

        SecurityContextHolder.setContext(securityContext);
    }


    // Positive test cases

    @Test
    @WithMockUser(username = "test_user")
    public void testGetCart() throws Exception {
        List<CartItemDTO> cartItems = Arrays.asList(
                new CartItemDTO(1L, "Pizza Margherita", "Medium", BigDecimal.valueOf(10.99), 2, "margherita.png"),
                new CartItemDTO(2L, "Pizza Pepperoni", "Large", BigDecimal.valueOf(12.99), 1, "pepperoni.png")
        );

        Mockito.when(cartService.getCart("test_user")).thenReturn(cartItems);

        mockMvc.perform(get("/cart").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pizzaId").value(1))
                .andExpect(jsonPath("$[0].name").value("Pizza Margherita"))
                .andExpect(jsonPath("$[0].size").value("Medium"))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[1].pizzaId").value(2))
                .andExpect(jsonPath("$[1].name").value("Pizza Pepperoni"))
                .andExpect(jsonPath("$[1].size").value("Large"))
                .andExpect(jsonPath("$[1].quantity").value(1));

        Mockito.verify(cartService, Mockito.times(1)).getCart("test_user");
    }

    @Test
    @WithMockUser(username = "test_user")
    public void testGetEmptyCart() throws Exception {
        Mockito.when(cartService.getCart("test_user")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/cart").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        Mockito.verify(cartService, Mockito.times(1)).getCart("test_user");
    }

    @Test
    @WithMockUser(username = "test_user")
    public void testSaveCart() throws Exception {
        Mockito.doNothing().when(cartService).saveCart(eq("test_user"), any());

        mockMvc.perform(post("/cart/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"pizzaId\":1,\"name\":\"Pizza Margherita\",\"size\":\"Medium\",\"price\":10.99,\"quantity\":2,\"image\":\"margherita.png\"}," +
                                "{\"pizzaId\":2,\"name\":\"Pizza Pepperoni\",\"size\":\"Large\",\"price\":12.99,\"quantity\":1,\"image\":\"pepperoni.png\"}]"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cart saved successfully"));

        Mockito.verify(cartService, Mockito.times(1)).saveCart(eq("test_user"), any());
    }

    @Test
    @WithMockUser(username = "test_user")
    public void testSyncCart() throws Exception {
        Mockito.doNothing().when(cartService).syncCart(eq("test_user"), any());

        mockMvc.perform(post("/cart/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"pizzaId\":1,\"name\":\"Pizza Margherita\",\"size\":\"Medium\",\"price\":10.99,\"quantity\":2,\"image\":\"margherita.png\"}," +
                                "{\"pizzaId\":2,\"name\":\"Pizza Pepperoni\",\"size\":\"Large\",\"price\":12.99,\"quantity\":1,\"image\":\"pepperoni.png\"}]"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cart synchronized successfully"));

        Mockito.verify(cartService, Mockito.times(1)).syncCart(eq("test_user"), any());
    }

    @Test
    @WithMockUser(username = "test_user")
    public void testAddToCart() throws Exception {
        Mockito.doNothing().when(cartService).addToCart(eq("test_user"), any());

        mockMvc.perform(post("/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pizzaId\":3,\"name\":\"Pizza Veggie\",\"size\":\"Small\",\"price\":9.99,\"quantity\":1,\"image\":\"veggie.png\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Item added to cart"));

        Mockito.verify(cartService, Mockito.times(1)).addToCart(eq("test_user"), any());
    }

    @Test
    @WithMockUser(username = "test_user")
    public void testUpdateQuantity() throws Exception {
        List<CartItemDTO> updatedCart = List.of(
                new CartItemDTO(1L, "Pizza Margherita", "Medium", BigDecimal.valueOf(10.99), 3, "margherita.png")
        );

        Mockito.when(cartService.updateQuantity(eq("test_user"), any())).thenReturn(updatedCart);

        mockMvc.perform(post("/cart/updateQuantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pizzaId\":1,\"size\":\"Medium\",\"delta\":3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pizzaId").value(1))
                .andExpect(jsonPath("$[0].quantity").value(3))
                .andExpect(jsonPath("$[0].name").value("Pizza Margherita"));

        Mockito.verify(cartService, Mockito.times(1)).updateQuantity(eq("test_user"), any());
    }
    // Negative test cases

    @Test
    public void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/cart").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test_user")
    public void testSaveCartWithInvalidPayload() throws Exception {
        String malformedJson = "{pizzaId:1, name:\"Pizza Margherita\", quantity:2";

        mockMvc.perform(post("/cart/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        Mockito.verify(cartService, Mockito.never()).saveCart(eq("test_user"), any());
    }


    @Test
    @WithMockUser(username = "test_user")
    public void testSyncCartWithEmptyPayload() throws Exception {
        mockMvc.perform(post("/cart/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cart synchronized successfully"));

        Mockito.verify(cartService, Mockito.times(1)).syncCart(eq("test_user"), any());
    }

    // Parameterized test for `addToCart`
    @ParameterizedTest
    @MethodSource("provideAddToCartData")
    @WithMockUser(username = "test_user")
    public void testAddToCartParameterized(String jsonPayload, String expectedMessage) throws Exception {
        Mockito.doNothing().when(cartService).addToCart(eq("test_user"), any());

        mockMvc.perform(post("/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));

        Mockito.verify(cartService, Mockito.times(1)).addToCart(eq("test_user"), any());
    }

    private static Stream<Object[]> provideAddToCartData() {
        return Stream.of(
                new Object[]{
                        "{\"pizzaId\":3,\"name\":\"Pizza Veggie\",\"size\":\"Small\",\"price\":9.99,\"quantity\":1,\"image\":\"veggie.png\"}",
                        "Item added to cart"
                },
                new Object[]{
                        "{\"pizzaId\":4,\"name\":\"Pizza BBQ\",\"size\":\"Medium\",\"price\":11.99,\"quantity\":2,\"image\":\"bbq.png\"}",
                        "Item added to cart"
                }
        );
    }
}
