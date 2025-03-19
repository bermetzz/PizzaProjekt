package whz.pti.eva.customer.boundary;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.DeliveryAddress;
import whz.pti.eva.customer.service.CustomerService;

@Controller
@RequestMapping("/delivery-addresses")
public class DeliveryAddressController {

    private final CustomerService customerService;

    public DeliveryAddressController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/add")
    public String addDeliveryAddressForm(Model model) {
        model.addAttribute("deliveryAddress", new DeliveryAddress());
        return "delivery-address-form";
    }

    @PostMapping("/add")
    public String saveNewDeliveryAddress(
            @ModelAttribute("deliveryAddress") DeliveryAddress deliveryAddress) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        Customer currentCustomer = customerService.getCustomerByUsername(auth.getName());
        customerService.addDeliveryAddress(currentCustomer.getId(), deliveryAddress);
        return "redirect:/profile";
    }

    @GetMapping("/edit/{id}")
    public String editDeliveryAddressForm(
            @PathVariable("id") Long id, Model model) {
        DeliveryAddress deliveryAddress = customerService.getDeliveryAddressById(id);
        if (deliveryAddress == null) {
            return "redirect:/profile";
        }
        model.addAttribute("deliveryAddress", deliveryAddress);
        return "delivery-address-form";
    }

    @PostMapping("/edit/{id}")
    public String updateDeliveryAddress(
            @PathVariable("id") Long id,
            @ModelAttribute("deliveryAddress") DeliveryAddress updatedAddress) {
        updatedAddress.setId(id);
        customerService.updateDeliveryAddress(id, updatedAddress);
        return "redirect:/profile";
    }

    @GetMapping("/delete/{id}")
    public String deleteDeliveryAddress(@PathVariable("id") Long id) {
        customerService.deleteDeliveryAddressById(id);
        return "redirect:/profile";
    }
}

