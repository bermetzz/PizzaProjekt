package whz.pti.eva.customer.boundary;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import whz.pti.eva.customer.domain.ChangePasswordForm;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.service.CustomerService;


@Controller
public class ProfileController {

    private final CustomerService customerService;

    public ProfileController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }
        Customer currentCustomer = customerService.getCustomerByUsername(auth.getName());

        model.addAttribute("customer", currentCustomer);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("customer") Customer updatedCustomer) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        Customer currentCustomer = customerService.getCustomerByUsername(auth.getName());

        customerService.updateCustomerProfile(currentCustomer.getId(), updatedCustomer);

        return "redirect:/profile?success=true";
    }

    @GetMapping("/change-password")
    public String showChangePasswordPage(Model model) {
        model.addAttribute("changePasswordForm", new ChangePasswordForm());
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @ModelAttribute("changePasswordForm") ChangePasswordForm form,
            Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        Customer currentCustomer = customerService.getCustomerByUsername(auth.getName());

        if (!customerService.checkPassword(currentCustomer.getUsername(), form.getOldPassword())) {
            model.addAttribute("error", "Old password is incorrect");
            return "change-password";
        }

        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match");
            return "change-password";
        }

        customerService.changePassword(currentCustomer.getId(), form.getNewPassword());
        return "redirect:/profile";
    }

}
