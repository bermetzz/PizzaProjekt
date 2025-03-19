package whz.pti.eva.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.DeliveryAddress;
import whz.pti.eva.customer.service.CustomerService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // The test profile to disable InitDatabase
public class DeliveryAddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Test
    public void testAddDeliveryAddressForm() throws Exception {
        mockMvc.perform(get("/delivery-addresses/add")
                        .with(user("testUser").roles("USER"))) // Simulate an authenticated user
                .andExpect(status().isOk())
                .andExpect(view().name("delivery-address-form"))
                .andExpect(model().attributeExists("deliveryAddress"));
    }

    @Test
    public void testSaveNewDeliveryAddress() throws Exception {
        Customer mockCustomer = new Customer();
        mockCustomer.setId(1L);

        Mockito.when(customerService.getCustomerByUsername("testUser")).thenReturn(mockCustomer);

        mockMvc.perform(post("/delivery-addresses/add")
                        .with(user("testUser").roles("USER"))
                        .param("street", "Str. New")
                        .param("city", "New York")
                        .param("postalCode", "9090")
                        .param("country", "US"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        Mockito.verify(customerService, Mockito.times(1))
                .addDeliveryAddress(Mockito.eq(1L), Mockito.any(DeliveryAddress.class));
    }

    @Test
    public void testEditDeliveryAddressForm() throws Exception {
        DeliveryAddress mockAddress = new DeliveryAddress();
        mockAddress.setId(1L);
        mockAddress.setStreet("Str. New");
        mockAddress.setCity("New York");
        mockAddress.setPostalCode("9090");
        mockAddress.setCountry("US");

        Mockito.when(customerService.getDeliveryAddressById(1L)).thenReturn(mockAddress);

        mockMvc.perform(get("/delivery-addresses/edit/1")
                        .with(user("testUser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("delivery-address-form"))
                .andExpect(model().attribute("deliveryAddress", mockAddress));
    }

    @Test
    public void testUpdateDeliveryAddress() throws Exception {
        DeliveryAddress updatedAddress = new DeliveryAddress();
        updatedAddress.setId(1L);
        updatedAddress.setStreet("Updated Street");
        updatedAddress.setCity("Updated City");
        updatedAddress.setPostalCode("12345");
        updatedAddress.setCountry("Updated Country");

        mockMvc.perform(post("/delivery-addresses/edit/1")
                        .with(user("testUser").roles("USER"))
                        .param("street", "Updated Street")
                        .param("city", "Updated City")
                        .param("postalCode", "12345")
                        .param("country", "Updated Country"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        Mockito.verify(customerService, Mockito.times(1))
                .updateDeliveryAddress(Mockito.eq(1L), Mockito.any(DeliveryAddress.class));
    }

    @Test
    public void testDeleteDeliveryAddress() throws Exception {
        mockMvc.perform(get("/delivery-addresses/delete/1")
                        .with(user("testUser").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        Mockito.verify(customerService, Mockito.times(1)).deleteDeliveryAddressById(1L);
    }
}
