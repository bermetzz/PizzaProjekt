package whz.pti.eva.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.service.CustomerService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private CommandLineRunner commandLineRunner; // Prevents `InitDatabase` issues

    @MockBean
    private SecurityContext securityContext;

    @MockBean
    private Authentication authentication;

    private Customer mockCustomer;

    @BeforeEach
    public void setUp() {
        mockCustomer = new Customer();
        mockCustomer.setId(1L);
        mockCustomer.setUsername("test_user");
        mockCustomer.setFirstName("Test");
        mockCustomer.setLastName("User");
        mockCustomer.setEmail("test@example.com");

        Mockito.when(authentication.getName()).thenReturn("test_user");
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(authentication.getPrincipal()).thenReturn(mockCustomer);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(customerService.getCustomerByUsername("test_user")).thenReturn(mockCustomer);
    }


    @Test
    public void testShowProfile() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("customer"))
                .andExpect(model().attribute("customer", mockCustomer));
    }

    @Test
    public void testUpdateProfile() throws Exception {
        mockMvc.perform(post("/profile/update")
                        .param("firstName", "Upd")
                        .param("lastName", "User")
                        .param("email", "updated@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?success=true"));

        Mockito.verify(customerService, Mockito.times(1))
                .updateCustomerProfile(Mockito.eq(1L), Mockito.any(Customer.class));
    }

    @Test
    public void testShowChangePasswordPage() throws Exception {
        mockMvc.perform(get("/change-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("change-password"))
                .andExpect(model().attributeExists("changePasswordForm"));
    }

    @Test
    public void testChangePasswordSuccess() throws Exception {
        Mockito.when(customerService.checkPassword("test_user", "old")).thenReturn(true);

        mockMvc.perform(post("/change-password")
                        .param("oldPassword", "old")
                        .param("newPassword", "new")
                        .param("confirmPassword", "new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        Mockito.verify(customerService, Mockito.times(1))
                .changePassword(Mockito.eq(1L), Mockito.eq("new"));
    }

    @Test
    public void testChangePasswordInvalidOldPassword() throws Exception {
        Mockito.when(customerService.checkPassword("test_user", "old")).thenReturn(false);

        mockMvc.perform(post("/change-password")
                        .param("oldPassword", "old")
                        .param("newPassword", "new")
                        .param("confirmPassword", "new"))
                .andExpect(status().isOk())
                .andExpect(view().name("change-password"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Old password is incorrect"));

        Mockito.verify(customerService, Mockito.never())
                .changePassword(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    public void testChangePasswordMismatch() throws Exception {
        Mockito.when(customerService.checkPassword("test_user", "old")).thenReturn(true);

        mockMvc.perform(post("/change-password")
                        .param("oldPassword", "old")
                        .param("newPassword", "new")
                        .param("confirmPassword", "diff"))
                .andExpect(status().isOk())
                .andExpect(view().name("change-password"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Passwords do not match"));

        Mockito.verify(customerService, Mockito.never())
                .changePassword(Mockito.anyLong(), Mockito.anyString());
    }
}
