package whz.pti.eva.admin.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.pti.eva.admin.service.AdminService;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.DeliveryAddress;
import whz.pti.eva.customer.service.CustomerService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final CustomerService customerService;
    private final AdminService adminService;

    @Autowired
    public AdminController(CustomerService customerService, AdminService adminService) {
        this.customerService = customerService;
        this.adminService = adminService;
    }

    @GetMapping
    public String redirectToCustomers() {
        return "redirect:/admin/customers";
    }

    @GetMapping("/customers")
    public String viewCustomers(Model model, Authentication auth) {
        if (auth != null) {
            model.addAttribute("user", customerService.getUserDTOByUsername(auth.getName()));
        }
        model.addAttribute("customers", customerService.getAllCustomers());
        return "customers";
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomerForm(@PathVariable Long id, Model model, Authentication auth) {
        Customer customer = adminService.getCustomerById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
        if (customer.getDeliveryAddresses() == null || customer.getDeliveryAddresses().isEmpty()) {
            customer.setDeliveryAddresses(List.of(new DeliveryAddress()));
        }
        if (auth != null) {
            model.addAttribute("user", customerService.getUserDTOByUsername(auth.getName()));
        }
        model.addAttribute("customer", customer);
        return "edit-customer";
    }


    @PostMapping("/customers/edit/{id}")
    public String updateCustomer(@PathVariable Long id, @ModelAttribute("customer") Customer updatedCustomer) {
        adminService.updateCustomer(id, updatedCustomer);
        return "redirect:/admin/customers";
    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        adminService.deleteCustomer(id);
        return "redirect:/admin/customers";
    }

    @GetMapping("/customers/new")
    public String createCustomerForm(Model model, Authentication auth) {
        Customer customer = new Customer();
        customer.setDeliveryAddresses(List.of(new DeliveryAddress()));
        if (auth != null) {
            model.addAttribute("user", customerService.getUserDTOByUsername(auth.getName()));
        }
        model.addAttribute("customer", customer);
        return "create-customer";
    }


    @PostMapping("/customers/new")
    public String createCustomer(@ModelAttribute("customer") Customer customer) {
        if (customer.getDeliveryAddresses() != null) {
            for (DeliveryAddress address : customer.getDeliveryAddresses()) {
                address.setCustomer(customer);
            }
        }
        customerService.register(customer);
        return "redirect:/admin/customers";
    }
}
