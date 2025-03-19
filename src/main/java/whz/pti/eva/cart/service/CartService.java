package whz.pti.eva.cart.service;

import whz.pti.eva.cart.domain.Cart;
import whz.pti.eva.cart.domain.CartItem;
import whz.pti.eva.cart.domain.CartItemDTO;
import whz.pti.eva.cart.domain.CartQuantityUpdateDTO;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    Cart getCartById(Long CartId);
    void addItemToCart(Long cartId, CartItem item);
    void removeItemFromCart(Long cartId, Long itemId);
    void clearCart(Long cartId);
    BigDecimal calculateTotal(Long cartId);
    Cart createCartForCustomer(Long customerId);
    Cart getCartByCustomerId(Long customerId);

    List<CartItemDTO> getCart(String username);
    void saveCart(String username, List<CartItemDTO> cartItems);
    void syncCart(String username, List<CartItemDTO> cartItems);
    void addToCart(String username, CartItemDTO cartItemDTO);
    List<CartItemDTO> updateQuantity(String username, CartQuantityUpdateDTO update);
}