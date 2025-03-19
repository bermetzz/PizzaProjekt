package whz.pti.eva.pizza.boundary;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import whz.pti.eva.customer.service.CustomerService;
import whz.pti.eva.pizza.service.PizzaService;

@Controller
@RequiredArgsConstructor
public class PizzaController {
    private final PizzaService pizzaService;
    private final CustomerService customerService;

    @GetMapping("/")
    public String showPizzas(Model model, Authentication auth) {
        model.addAttribute("pizzas", pizzaService.getAllPizzas());
        if (auth != null) {
            // Fetch user and cart
            model.addAttribute("user", customerService.getUserDTOByUsername(auth.getName()));
            model.addAttribute("cartItems", customerService.getCartItemsByUsername(auth.getName()));
            System.out.println(customerService.getCartItemsByUsername(auth.getName()));
            System.out.println("cart items from pizza controller: " + customerService.getCartItemsByUsername(auth.getName()));
        } else {
            System.out.println("auth is null");
        }
        return "index";
    }

}