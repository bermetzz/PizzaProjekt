package whz.pti.eva.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.CustomerRepository;
import whz.pti.eva.customer.domain.Roles;
import whz.pti.eva.customer.service.CustomerUserDetailsService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerUserDetailsServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetailsService = new CustomerUserDetailsService(customerRepository);
    }

    @Test
    void testLoadUserByUsername_Success() {
        Customer customer = new Customer();
        customer.setUsername("testuser");
        customer.setPassword("testpassword");
        customer.setRole(Roles.USER);

        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(customer));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("testpassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

        verify(customerRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        when(customerRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("unknown"));

        verify(customerRepository, times(1)).findByUsername("unknown");
    }
}
