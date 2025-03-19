package whz.pti.eva.customer.service;

import whz.pti.eva.cart.domain.CartItemDTO;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.CustomerDTO;
import whz.pti.eva.customer.domain.DeliveryAddress;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    void register(Customer customer);
    Customer getCustomerByUsername(String username);
    void updateCustomerProfile(Long customerId, Customer updatedCustomer);
    CustomerDTO getUserDTOByUsername(String username);
    List<CartItemDTO> getCartItemsByUsername(String username);

    boolean checkPassword(String username, String oldPassword);
    void changePassword(Long customerId, String newPassword);
    void addDeliveryAddress(Long customerId, DeliveryAddress deliveryAddress);
    DeliveryAddress getDeliveryAddressById(Long addressId);
    void updateDeliveryAddress(Long addressId, DeliveryAddress updatedAddress);
    void deleteDeliveryAddressById(Long addressId);


    List<Customer> getAllCustomers();

    Optional<Customer> getCustomerById(Long id);

    void updateCustomer (Long id, Customer updatedCustomer);

    void deleteCustomer(Long id);

    List<DeliveryAddress> getAddressesByUsername(String username);

    Customer registerAndReturn(Customer customer);

}