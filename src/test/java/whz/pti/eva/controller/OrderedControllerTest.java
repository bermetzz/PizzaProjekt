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
import whz.pti.eva.customer.domain.CustomerDTO;
import whz.pti.eva.customer.service.CustomerService;
import whz.pti.eva.ordered.service.OrderedService;


import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // The test profile to disable InitDatabase
public class OrderedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private OrderedService orderedService;

    private CustomerDTO mockUserDTO;

    @BeforeEach
    void setUp() {
        mockUserDTO = CustomerDTO.builder()
                .username("test_user")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .role("USER")
                .addresses("123 Main St, City, Country")
                .build();

        Mockito.when(customerService.getUserDTOByUsername("test_user")).thenReturn(mockUserDTO);
    }

    @Test
    @WithMockUser(username = "test_user")
    public void testShowOrdersAuthenticated() throws Exception {
        mockMvc.perform(get("/order"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", mockUserDTO));

        Mockito.verify(customerService, Mockito.times(1)).getUserDTOByUsername("test_user");
    }

    @Test
    public void testShowOrdersUnauthenticated() throws Exception {
        mockMvc.perform(get("/order"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        Mockito.verify(customerService, Mockito.never()).getUserDTOByUsername(Mockito.anyString());
    }

    @Test
    @WithMockUser(username = "test_user")
    public void testPlaceOrder() throws Exception {
        Mockito.doAnswer(invocation -> null).when(orderedService).placeOrder("test_user", 1L);

        mockMvc.perform(post("/order")
                        .param("addressId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/order"));

        Mockito.verify(orderedService, Mockito.times(1)).placeOrder("test_user", 1L);
    }

    @Test
    public void testPlaceOrderUnauthenticated() throws Exception {
        mockMvc.perform(post("/order")
                        .param("addressId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        Mockito.verify(orderedService, Mockito.never()).placeOrder(Mockito.anyString(), anyLong());
    }
}
