package whz.pti.eva.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import whz.pti.eva.cart.domain.*;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.CustomerRepository;
import whz.pti.eva.pizza.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final PizzaRepository pizzaRepository;
    private final PriceRepository priceRepository;


    @Override
    public Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Cart is not found!"));
    }

    @Override
    public void addItemToCart(Long cartId, CartItem item) {
        Cart cart = getCartById(cartId);

        //check if the pizza is already in the cart
        CartItem existingItem = cart.getItems().stream()
                .filter(cartItem -> cartItem.getPizza().getId().equals(item.getPizza().getId()))
                .findFirst().orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
        } else {
            item.setCart(cart);
            cartItemRepository.save(item);
            cart.getItems().add(item);
        }
        cartRepository.save(cart);
    }

    @Override
    public void removeItemFromCart(Long cartId, Long itemId) {
        Cart cart = getCartById(cartId);
        cart.getItems().removeIf(i -> i.getId().equals(itemId));
        cartRepository.save(cart);
    }

    @Override
    public void clearCart(Long cartId) {
        Cart cart = getCartById(cartId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public BigDecimal calculateTotal(Long cartId) {
        Cart cart = getCartById(cartId);
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            BigDecimal itemTotal = item.getPrice().getAmount().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);
        }
        return total;
    }


    @Override
    public Cart createCartForCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Cart cart = new Cart();
        cart.setCustomer(customer);
        return cartRepository.save(cart);
    }

    @Override
    public Cart getCartByCustomerId(Long customerId) {
        return cartRepository.findByCustomerId(customerId).orElseThrow(
                () -> new RuntimeException("For the customer " + customerId + " cart is not found."));
    }

    @Override
    public List<CartItemDTO> getCart(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "User not logged in"));

        Cart cart = cartRepository.findByCustomer(customer).orElse(new Cart());

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
    public void saveCart(String username, List<CartItemDTO> cartItems) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "User not logged in"));

        Cart cart = cartRepository.findByCustomer(customer).orElse(new Cart());
        cart.setCustomer(customer);

        List<CartItem> items = cartItems.stream().map(dto -> {
            CartItem item = new CartItem();
            item.setQuantity(dto.getQuantity());

            Pizza pizza = pizzaRepository.findById(dto.getPizzaId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid pizza ID"));

            Price price = priceRepository.findByPizzaAndSize(pizza, PizzaSize.valueOf(dto.getSize().toUpperCase()))
                    .orElseThrow(() -> new IllegalArgumentException("Invalid size for pizza"));

            item.setPizza(pizza);
            item.setPrice(price);
            item.setCart(cart);
            return item;
        }).collect(Collectors.toList());

        cart.setItems(items);
        cartRepository.save(cart);
    }

    @Override
    public void syncCart(String username, List<CartItemDTO> cartItems) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "User not logged in"));

        Cart cart = cartRepository.findByCustomer(customer).orElse(new Cart());
        cart.setCustomer(customer);

        for (CartItemDTO dto : cartItems) {
            Pizza pizza = pizzaRepository.findById(dto.getPizzaId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid pizza ID"));

            Price price = priceRepository.findByPizzaAndSize(pizza, PizzaSize.valueOf(dto.getSize().toUpperCase()))
                    .orElseThrow(() -> new IllegalArgumentException("Invalid size for pizza"));

            CartItem existingItem = cart.getItems().stream()
                    .filter(item -> item.getPizza().equals(pizza) && item.getPrice().equals(price))
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());
            } else {
                CartItem newItem = new CartItem();
                newItem.setPizza(pizza);
                newItem.setPrice(price);
                newItem.setQuantity(dto.getQuantity());
                newItem.setCart(cart);
                cart.getItems().add(newItem);
            }
        }

        cartRepository.save(cart);
    }

    @Override
    public void addToCart(String username, CartItemDTO cartItemDTO) {
        syncCart(username, List.of(cartItemDTO));
    }

    @Override
    public List<CartItemDTO> updateQuantity(String username, CartQuantityUpdateDTO update) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "User not logged in"));

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getPizza().getId().equals(update.getPizzaId()) &&
                        ci.getPrice().getSize().toString().equalsIgnoreCase(update.getSize()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

        item.setQuantity(item.getQuantity() + update.getDelta());
        if (item.getQuantity() <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        }

        cartRepository.save(cart);

        return cart.getItems().stream()
                .map(ci -> new CartItemDTO(
                        ci.getPizza().getId(),
                        ci.getPizza().getName(),
                        ci.getPrice().getSize().toString(),
                        ci.getPrice().getAmount(),
                        ci.getQuantity(),
                        ci.getPizza().getImagePath()
                ))
                .collect(Collectors.toList());
    }

}