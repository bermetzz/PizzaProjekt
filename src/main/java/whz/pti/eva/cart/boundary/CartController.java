package whz.pti.eva.cart.boundary;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import whz.pti.eva.cart.domain.*;
import whz.pti.eva.cart.service.CartService;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.CustomerRepository;
import whz.pti.eva.pizza.domain.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<CartItemDTO>> getCart(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(cartService.getCart(auth.getName()));
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<String> saveCart(@RequestBody List<CartItemDTO> cartItems, Authentication auth) {
        cartService.saveCart(auth.getName(), cartItems);
        return ResponseEntity.ok("Cart saved successfully");
    }

    @PostMapping("/sync")
    @ResponseBody
    public ResponseEntity<String> syncCart(@RequestBody List<CartItemDTO> cartItems, Authentication auth) {
        cartService.syncCart(auth.getName(), cartItems);
        return ResponseEntity.ok("Cart synchronized successfully");
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<String> addToCart(@RequestBody CartItemDTO cartItemDTO, Authentication auth) {
        cartService.addToCart(auth.getName(), cartItemDTO);
        return ResponseEntity.ok("Item added to cart");
    }

    @PostMapping("/updateQuantity")
    @ResponseBody
    public ResponseEntity<List<CartItemDTO>> updateQuantity(@RequestBody CartQuantityUpdateDTO update, Authentication auth) {
        return ResponseEntity.ok(cartService.updateQuantity(auth.getName(), update));
    }
}

