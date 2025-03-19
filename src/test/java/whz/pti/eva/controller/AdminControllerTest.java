package whz.pti.eva.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import whz.pti.eva.admin.boundary.AdminController;
import whz.pti.eva.admin.service.AdminService;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.CustomerDTO;
import whz.pti.eva.customer.domain.DeliveryAddress;
import whz.pti.eva.customer.service.CustomerService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @Mock
    private AdminService adminService;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminController = new AdminController(customerService, adminService);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void testRedirectToCustomers() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/customers"));
    }

    @Test
    void testEditCustomerForm() throws Exception {
        Customer customer = new Customer();
        customer.setId(1L);

        when(adminService.getCustomerById(1L)).thenReturn(Optional.of(customer));
        when(customerService.getUserDTOByUsername("admin")).thenReturn(null);

        mockMvc.perform(get("/admin/customers/edit/1").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-customer"))
                .andExpect(model().attributeExists("customer"));

        verify(adminService, times(1)).getCustomerById(1L);
    }

    @Test
    void testUpdateCustomer() throws Exception {
        Customer customer = new Customer();

        mockMvc.perform(post("/admin/customers/edit/1")
                        .flashAttr("customer", customer))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/customers"));

        verify(adminService, times(1)).updateCustomer(eq(1L), any(Customer.class));
    }

    @Test
    void testDeleteCustomer() throws Exception {
        mockMvc.perform(get("/admin/customers/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/customers"));

        verify(adminService, times(1)).deleteCustomer(1L);
    }

    @Test
    void testCreateCustomerForm() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin");

        CustomerDTO mockUserDTO = new CustomerDTO();
        mockUserDTO.setFirstName("Admin");
        mockUserDTO.setLastName("User");
        when(customerService.getUserDTOByUsername(anyString())).thenReturn(new CustomerDTO());
        when(customerService.getAllCustomers()).thenReturn(List.of(new Customer()));

        mockMvc.perform(get("/admin/customers/new").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("create-customer"))
                .andExpect(model().attributeExists("customer"))
                .andExpect(model().attributeExists("user"));

        verify(customerService, times(1)).getUserDTOByUsername("admin");
    }


    @Test
    void testCreateCustomer() throws Exception {
        Customer customer = new Customer();

        mockMvc.perform(post("/admin/customers/new")
                        .flashAttr("customer", customer))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/customers"));

        verify(customerService, times(1)).register(any(Customer.class));
    }
}
