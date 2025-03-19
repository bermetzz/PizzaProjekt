package whz.pti.eva.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import whz.pti.eva.cart.domain.*;
import whz.pti.eva.cart.service.CartServiceImpl;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.CustomerRepository;
import whz.pti.eva.pizza.domain.Pizza;
import whz.pti.eva.pizza.domain.PizzaRepository;
import whz.pti.eva.pizza.domain.Price;
import whz.pti.eva.pizza.domain.PriceRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceImplTest {

    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PizzaRepository pizzaRepository;

    @Mock
    private PriceRepository priceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartService = new CartServiceImpl(cartRepository, cartItemRepository, customerRepository, pizzaRepository, priceRepository);
    }

    @Test
    void testGetCartById() {
        Cart cart = new Cart();
        cart.setId(1L);
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        Cart result = cartService.getCartById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(cartRepository, times(1)).findById(1L);
    }

    @Test
    void testAddItemToCart_NewItem() {
        Cart cart = new Cart();
        cart.setId(1L);

        CartItem item = new CartItem();
        item.setPizza(new Pizza());
        item.setQuantity(2);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(item);

        cartService.addItemToCart(1L, item);

        assertEquals(1, cart.getItems().size());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testRemoveItemFromCart() {
        Cart cart = new Cart();
        CartItem item = new CartItem();
        item.setId(1L);
        cart.getItems().add(item);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        cartService.removeItemFromCart(1L, 1L);

        assertTrue(cart.getItems().isEmpty());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testGetCartByCustomerId() {
        Customer customer = new Customer();
        customer.setId(1L);

        Cart cart = new Cart();
        cart.setCustomer(customer);

        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cart));

        Cart result = cartService.getCartByCustomerId(1L);

        assertNotNull(result);
        assertEquals(customer, result.getCustomer());
        verify(cartRepository, times(1)).findByCustomerId(1L);
    }
}
