package whz.pti.eva.customer.boundary;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.pti.eva.config.AuthenticationManagerConfig;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.service.CustomerService;
import whz.pti.eva.payment.service.PaymentService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final CustomerService customerService;
    private final PaymentService paymentService;

    @GetMapping("/login")
    public String login(Model model,
                        @RequestParam(required = false, defaultValue = "false") Boolean error) {
        model.addAttribute("error",error);
        return "login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("customer") Customer customer) {
        Customer savedCustomer = customerService.registerAndReturn(customer);
        paymentService.createAccount(savedCustomer.getId());
        return "redirect:/login";
    }
}
