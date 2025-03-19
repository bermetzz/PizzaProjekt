package whz.pti.eva.customer.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whz.pti.eva.cart.domain.Cart;
import whz.pti.eva.cart.domain.CartItemDTO;
import whz.pti.eva.cart.domain.CartRepository;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.CustomerDTO;
import whz.pti.eva.customer.domain.CustomerRepository;
import whz.pti.eva.customer.domain.DeliveryAddress;
import whz.pti.eva.customer.domain.Roles;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;

    @Override
    public void register(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setEnabled(true);
        customerRepository.save(customer);
    }

    @Override
    public Customer registerAndReturn(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setEnabled(true);
        Customer savedCustomer = customerRepository.save(customer);

        // Create a cart after registration
        Cart cart = new Cart();
        cart.setCustomer(savedCustomer);
        cartRepository.save(cart);

        return savedCustomer;
    }


    @Override
    public Customer getCustomerByUsername(String username) {
        return customerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
    }

    @Override
    @Transactional
    public void updateCustomerProfile(Long id, Customer updatedCustomer) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (updatedCustomer.getFirstName() != null) {
            existingCustomer.setFirstName(updatedCustomer.getFirstName());
        }
        if (updatedCustomer.getLastName() != null) {
            existingCustomer.setLastName(updatedCustomer.getLastName());
        }
        if (updatedCustomer.getEmail() != null) {
            existingCustomer.setEmail(updatedCustomer.getEmail());
        }

        if (updatedCustomer.getUsername() != null) {
            existingCustomer.setUsername(updatedCustomer.getUsername());
        }

        customerRepository.save(existingCustomer);
    }

    public CustomerDTO getUserDTOByUsername(String username) {
        Customer customer=customerRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("Username was not found: "+username));

        return CustomerDTO.builder()
                .email(customer.getEmail())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .username(customer.getUsername())
                .role(customer.getRole().name())
                .addresses(customer.getDeliveryAddresses().stream().map(address -> String.format("%s, %s, %s, %s",
                                address.getCountry(),
                                address.getCity(),
                                address.getStreet(),
                                address.getPostalCode()))
                        .collect(Collectors.joining("\n")))
                .build();

    }

    @Override
    public List<CartItemDTO> getCartItemsByUsername(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Find or create a cart for the customer
        Cart cart = cartRepository.findByCustomer(customer)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    return cartRepository.save(newCart); // Save the new cart to the database
                });

        // Map cart items to DTOs
        return cart.getItems().stream()
                .map(item -> new CartItemDTO(
                        item.getPizza().getId(),
                        item.getPizza().getName(),
                        item.getPrice().getSize().toString(),
                        item.getPrice().getAmount(),
                        item.getQuantity(),
                        item.getPizza().getImagePath()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkPassword(String username, String oldPassword) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        return passwordEncoder.matches(oldPassword, customer.getPassword());
    }

    @Override
    @Transactional
    public void changePassword(Long customerId, String newPassword) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        customer.setPassword(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);
    }


    @Override
    @Transactional
    public void addDeliveryAddress(Long customerId, DeliveryAddress deliveryAddress) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

        if (customer.getDeliveryAddresses() == null) {
            customer.setDeliveryAddresses(new ArrayList<>());
        }

        boolean exists = customer.getDeliveryAddresses().stream()
                .anyMatch(address -> address.equals(deliveryAddress));

        if (!exists) {
            deliveryAddress.setCustomer(customer);
            customer.getDeliveryAddresses().add(deliveryAddress);
            customerRepository.save(customer);
        }
    }


    @Override
    public DeliveryAddress getDeliveryAddressById(Long addressId) {
        return customerRepository.findAll().stream()
                .flatMap(c -> c.getDeliveryAddresses().stream())
                .filter(address -> address.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));
    }

    @Override
    @Transactional
    public void updateDeliveryAddress(Long addressId, DeliveryAddress updatedAddress) {
        DeliveryAddress existingAddress = getDeliveryAddressById(addressId);

        existingAddress.setStreet(updatedAddress.getStreet());
        existingAddress.setCity(updatedAddress.getCity());
        existingAddress.setPostalCode(updatedAddress.getPostalCode());
        existingAddress.setCountry(updatedAddress.getCountry());

//        customerRepository.save(existingAddress.getCustomer());
    }

    @Override
    @Transactional
    public void deleteDeliveryAddressById(Long addressId) {
        DeliveryAddress address = customerRepository.findAll().stream()
                .flatMap(customer -> customer.getDeliveryAddresses().stream())
                .filter(addr -> addr.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        Customer customer = address.getCustomer();
        customer.getDeliveryAddresses().removeIf(addr -> addr.getId().equals(addressId));

        customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers () {
            return customerRepository.findAll();
        }

        @Override
        public Optional<Customer> getCustomerById (Long id){
            return customerRepository.findById(id);
        }

        @Override
        public void updateCustomer (Long id, Customer updatedCustomer){
            Customer existingCustomer = customerRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));

            existingCustomer.setFirstName(updatedCustomer.getFirstName());
            existingCustomer.setLastName(updatedCustomer.getLastName());
            existingCustomer.setEmail(updatedCustomer.getEmail());
            existingCustomer.setUsername(updatedCustomer.getUsername());
            existingCustomer.setRole(updatedCustomer.getRole());

            customerRepository.save(existingCustomer);
        }

        @Override
        public void deleteCustomer (Long id){
            customerRepository.deleteById(id);

        }
    @Override
    public List<DeliveryAddress> getAddressesByUsername(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        return customer.getDeliveryAddresses();
    }
}