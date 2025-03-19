package whz.pti.eva.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import whz.pti.eva.customer.domain.DeliveryAddress;
import whz.pti.eva.customer.service.CustomerService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // The test profile to disable InitDatabase
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    private List<DeliveryAddress> mockAddresses;

    @BeforeEach
    void setUp() {
        DeliveryAddress address1 = DeliveryAddress.builder()
                .street("123 Main St")
                .city("Test City")
                .postalCode("12345")
                .country("Test Country")
                .build();
        address1.setId(1L);

        DeliveryAddress address2 = DeliveryAddress.builder()
                .street("456 Elm St")
                .city("Another City")
                .postalCode("67890")
                .country("Another Country")
                .build();
        address2.setId(2L);

        mockAddresses = Arrays.asList(address1, address2);
    }

    @Test
    @WithMockUser(username = "test_user")
    public void testGetAddressesAuthenticatedWithAddresses() throws Exception {
        Mockito.when(customerService.getAddressesByUsername(anyString())).thenReturn(mockAddresses);

        mockMvc.perform(get("/customer/addresses").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)) // Expect 2 addresses
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].street").value("123 Main St"))
                .andExpect(jsonPath("$[0].city").value("Test City"))
                .andExpect(jsonPath("$[0].postalCode").value("12345"))
                .andExpect(jsonPath("$[0].country").value("Test Country"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].street").value("456 Elm St"))
                .andExpect(jsonPath("$[1].city").value("Another City"))
                .andExpect(jsonPath("$[1].postalCode").value("67890"))
                .andExpect(jsonPath("$[1].country").value("Another Country"));

        Mockito.verify(customerService, Mockito.times(1)).getAddressesByUsername("test_user");
    }

    @Test
    @WithMockUser(username = "test_user")
    public void testGetAddressesAuthenticatedWithNoAddresses() throws Exception {
        Mockito.when(customerService.getAddressesByUsername(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/customer/addresses").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0)); // Expect empty list

        Mockito.verify(customerService, Mockito.times(1)).getAddressesByUsername("test_user");
    }

    @Test
    public void testGetAddressesUnauthenticated() throws Exception {
        mockMvc.perform(get("/customer/addresses").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()); // Expect HTTP 401 Unauthorized

        Mockito.verify(customerService, Mockito.never()).getAddressesByUsername(Mockito.anyString());
    }
}
