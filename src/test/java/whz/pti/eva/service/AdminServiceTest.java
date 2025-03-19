package whz.pti.eva.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;
import whz.pti.eva.admin.service.AdminService;
import whz.pti.eva.cart.domain.CartRepository;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.CustomerRepository;
import whz.pti.eva.customer.domain.DeliveryAddress;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CartRepository cartRepository;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminService = new AdminService(customerRepository, cartRepository);
    }

    @Test
    void testGetAllCustomers() {
        List<Customer> mockCustomers = List.of(new Customer(), new Customer());
        when(customerRepository.findAll()).thenReturn(mockCustomers);

        List<Customer> customers = adminService.getAllCustomers();

        assertEquals(2, customers.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testCreateCustomer() {
        Customer mockCustomer = new Customer();
        when(customerRepository.save(mockCustomer)).thenReturn(mockCustomer);

        Customer savedCustomer = adminService.createCustomer(mockCustomer);

        assertNotNull(savedCustomer);
        verify(customerRepository, times(1)).save(mockCustomer);
    }

    @Test
    void testGetCustomerById_Success() {
        Customer mockCustomer = new Customer();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));

        Optional<Customer> customer = adminService.getCustomerById(1L);

        assertTrue(customer.isPresent());
        assertEquals(mockCustomer, customer.get());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCustomerById_NotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Customer> customer = adminService.getCustomerById(1L);

        assertTrue(customer.isEmpty());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional
    void testUpdateCustomer() {
        Customer existingCustomer = new Customer();
        existingCustomer.setDeliveryAddresses(new ArrayList<>());

        Customer updatedCustomer = new Customer();
        updatedCustomer.setFirstName("John");
        updatedCustomer.setLastName("Doe");
        updatedCustomer.setEmail("john.doe@example.com");
        updatedCustomer.setDeliveryAddresses(List.of(new DeliveryAddress()));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));

        adminService.updateCustomer(1L, updatedCustomer);

        assertEquals("John", existingCustomer.getFirstName());
        assertEquals("Doe", existingCustomer.getLastName());
        assertEquals("john.doe@example.com", existingCustomer.getEmail());
        assertEquals(1, existingCustomer.getDeliveryAddresses().size());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(existingCustomer);
    }

    @Test
    @Transactional
    void testDeleteCustomer() {
        Customer mockCustomer = new Customer();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));

        adminService.deleteCustomer(1L);

        verify(cartRepository, times(1)).deleteByCustomer(mockCustomer);
        verify(customerRepository, times(1)).delete(mockCustomer);
    }

    @Test
    @Transactional
    void testDeleteCustomer_NotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> adminService.deleteCustomer(1L));

        verify(cartRepository, never()).deleteByCustomer(any());
        verify(customerRepository, never()).delete(any());
    }
}
