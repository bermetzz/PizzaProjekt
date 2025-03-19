package whz.pti.eva.ordered.boundary;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import whz.pti.eva.cart.domain.Cart;
import whz.pti.eva.cart.service.CartService;
import whz.pti.eva.customer.domain.ChangePasswordForm;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.service.CustomerService;
import whz.pti.eva.ordered.domain.OrderDTO;
import whz.pti.eva.ordered.domain.Ordered;
import whz.pti.eva.ordered.service.OrderedService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import whz.pti.eva.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderedController {
    private final CustomerService customerService;
    private final OrderedService orderedService;
    private final PaymentService paymentService;
    private final CartService cartService;

    @GetMapping
    public String showOrders(Model model, Authentication auth) {
        if (auth != null) {
            List<OrderDTO> orders = orderedService.getOrderDTOsByCustomer(auth.getName());
            model.addAttribute("user", customerService.getUserDTOByUsername(auth.getName()));
            model.addAttribute("orders", orders);
        } else {
            System.out.println("auth is null");
        }

        return "order";
    }

    @PostMapping
    public String placeOrder(@RequestParam Long addressId, Authentication auth, Model model) {
        if (auth == null) {
            model.addAttribute("error", "User not authenticated.");
            return "redirect:/login";
        }

        Customer customer = customerService.getCustomerByUsername(auth.getName());

        // Calculate the total order amount
        BigDecimal orderAmount = BigDecimal.valueOf(orderedService.calculateOrderTotal(auth.getName()));
        BigDecimal balance = BigDecimal.valueOf(paymentService.getBalance(customer.getId()));

        // Compare balances properly (BigDecimal)
        if (balance.compareTo(orderAmount) >= 0) {
            Ordered order = orderedService.placeOrder(auth.getName(), addressId);
            paymentService.processPayment(customer.getId(), orderAmount);
            return "redirect:/order";
        } else {
            model.addAttribute("error", "Insufficient funds. Please top up your account.");
            return "order";
        }
    }




    @PostMapping("/checkout")
    public String checkout(@RequestParam Long addressId, Authentication auth, Model model) {
        if (auth == null) {
            model.addAttribute("error", "You need to log in to proceed.");
            return "redirect:/login";
        }

        Customer customer = customerService.getCustomerByUsername(auth.getName());
        Cart cart = cartService.getCartByCustomerId(customer.getId());

        BigDecimal cartTotal = cartService.calculateTotal(cart.getId());
        Double balance = paymentService.getBalance(customer.getId());

        if (balance >= cartTotal.doubleValue()) {
            Ordered order = orderedService.placeOrder(auth.getName(), addressId);
            paymentService.processPayment(customer.getId(), BigDecimal.valueOf(cartTotal.doubleValue()));
            model.addAttribute("success", "Order placed successfully!");
            return "orderConfirmation";
        } else {
            model.addAttribute("error", "Insufficient funds. Please top-up your account.");
            return "cart";
        }
    }


}