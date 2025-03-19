package whz.pti.eva.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.service.CustomerService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private CommandLineRunner commandLineRunner; // Prevents `InitDatabase` issues

    @Test
    public void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("error", false));
        System.out.println("Login page loaded successfully without errors");
    }

    @Test
    public void testLoginPageWithError() throws Exception {
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("error", true));

        System.out.println("Login page loaded successfully with error parameter");
    }

    @Test
    public void testRegisterForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        System.out.println("Register form loaded successfully");
    }

    @Test
    public void testRegisterUser() throws Exception {
        Customer testCustomer = new Customer();
        testCustomer.setUsername("test_user");
        testCustomer.setPassword("password");
        testCustomer.setEmail("test@example.com");
        testCustomer.setFirstName("Test");
        testCustomer.setLastName("User");

        Mockito.doNothing().when(customerService).register(Mockito.any(Customer.class));

        mockMvc.perform(post("/register")
                        .flashAttr("customer", testCustomer))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        Mockito.verify(customerService, Mockito.times(1)).register(Mockito.any(Customer.class));
    }

}
