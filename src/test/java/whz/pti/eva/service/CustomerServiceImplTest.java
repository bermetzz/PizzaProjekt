package whz.pti.eva.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import whz.pti.eva.cart.domain.Cart;
import whz.pti.eva.cart.domain.CartRepository;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.CustomerDTO;
import whz.pti.eva.customer.domain.CustomerRepository;
import whz.pti.eva.customer.domain.DeliveryAddress;
import whz.pti.eva.customer.service.CustomerServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CartRepository cartRepository;

    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerService = new CustomerServiceImpl(customerRepository, passwordEncoder, cartRepository);
    }

    @Test
    void testRegister() {
        Customer customer = new Customer();
        customer.setPassword("rawPassword");

        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        customerService.register(customer);

        assertEquals("encodedPassword", customer.getPassword());
        verify(customerRepository, times(1)).save(customer);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testGetCustomerByUsername() {
        Customer customer = new Customer();
        customer.setUsername("testUser");

        when(customerRepository.findByUsername("testUser")).thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerByUsername("testUser");

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        verify(customerRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testUpdateCustomerProfile() {
        Customer existingCustomer = new Customer();
        existingCustomer.setId(1L);
        existingCustomer.setFirstName("OldName");

        Customer updatedCustomer = new Customer();
        updatedCustomer.setFirstName("NewName");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));

        customerService.updateCustomerProfile(1L, updatedCustomer);

        assertEquals("NewName", existingCustomer.getFirstName());
        verify(customerRepository, times(1)).save(existingCustomer);
    }

    @Test
    void testGetUserDTOByUsername() {
        Customer customer = new Customer();
        customer.setUsername("testUser");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");

        when(customerRepository.findByUsername("testUser")).thenReturn(Optional.of(customer));

        CustomerDTO result = customerService.getUserDTOByUsername("testUser");

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(customerRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testGetAllCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of(new Customer(), new Customer()));

        List<Customer> customers = customerService.getAllCustomers();

        assertEquals(2, customers.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testDeleteCustomer() {
        customerService.deleteCustomer(1L);

        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    void testAddDeliveryAddress() {
        Customer customer = new Customer();
        customer.setDeliveryAddresses(new ArrayList<>());

        DeliveryAddress address = new DeliveryAddress();
        address.setStreet("Test Street");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        customerService.addDeliveryAddress(1L, address);

        assertEquals(1, customer.getDeliveryAddresses().size());
        assertEquals(address, customer.getDeliveryAddresses().get(0));
        verify(customerRepository, times(1)).save(customer);
    }


    @Test
    void testChangePassword() {
        Customer customer = new Customer();
        customer.setPassword("oldPassword");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        customerService.changePassword(1L, "newPassword");

        assertEquals("encodedPassword", customer.getPassword());
        verify(customerRepository, times(1)).save(customer);
    }
}
