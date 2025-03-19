package whz.pti.eva.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import whz.pti.eva.cart.domain.*;
import whz.pti.eva.customer.domain.*;
import whz.pti.eva.ordered.domain.*;
import whz.pti.eva.ordered.service.OrderedServiceImpl;
import whz.pti.eva.pizza.domain.Pizza;
import whz.pti.eva.pizza.domain.Price;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class OrderedServiceImplTest {

    private OrderedServiceImpl orderedService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private OrderedRepository orderedRepository;

    @Mock
    private DeliveryAddressRepository deliveryAddressRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderedService = new OrderedServiceImpl(
                customerRepository, cartRepository, cartItemRepository, orderedRepository, deliveryAddressRepository);
    }

    @Test
    void testPlaceOrderSuccess() {
        Customer customer = new Customer();
        customer.setUsername("testuser");

        DeliveryAddress address = new DeliveryAddress();
        address.setId(1L);

        CartItem cartItem = new CartItem();
        cartItem.setQuantity(2);

        Price price = new Price();
        price.setAmount(BigDecimal.valueOf(10.0));
        cartItem.setPrice(price);

        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setItems(List.of(cartItem));

        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomer(customer)).thenReturn(Optional.of(cart));
        when(deliveryAddressRepository.findById(1L)).thenReturn(Optional.of(address));

        Ordered order = orderedService.placeOrder("testuser", 1L);

        assertNotNull(order);
        assertEquals(1, order.getItems().size());
        verify(cartRepository).save(cart);
        assertTrue(cart.getItems().isEmpty());
        verify(orderedRepository).save(order);
    }

    @Test
    void testPlaceOrderCustomerNotFound() {
        when(customerRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderedService.placeOrder("nonexistent", 1L);
        });

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void testPlaceOrderEmptyCart() {
        Customer customer = new Customer();
        customer.setUsername("testuser");

        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomer(customer)).thenReturn(Optional.of(new Cart()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderedService.placeOrder("testuser", 1L);
        });

        assertEquals("Cart is empty", exception.getMessage());
    }

    @Test
    void testGetOrdersByCustomer() {
        Customer customer = new Customer();
        customer.setUsername("testuser");

        Ordered order1 = new Ordered();
        Ordered order2 = new Ordered();

        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(customer));
        when(orderedRepository.findByCustomerOrderByCreatedAtDesc(customer)).thenReturn(List.of(order1, order2));

        List<Ordered> orders = orderedService.getOrdersByCustomer("testuser");

        assertEquals(2, orders.size());
        verify(orderedRepository).findByCustomerOrderByCreatedAtDesc(customer);
    }
}