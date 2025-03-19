package whz.pti.eva.customer.boundary;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import whz.pti.eva.customer.domain.DeliveryAddress;
import whz.pti.eva.customer.service.CustomerService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/addresses")
    public ResponseEntity<List<Map<String, String>>> getAddresses(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<DeliveryAddress> addresses = customerService.getAddressesByUsername(auth.getName());

        // If no addresses, return an empty list (JSON array)
        if (addresses.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // Transform addresses into a simplified JSON format
        List<Map<String, String>> addressList = addresses.stream()
                .map(address -> Map.of(
                        "id", String.valueOf(address.getId()),
                        "street", address.getStreet(),
                        "city", address.getCity(),
                        "postalCode", address.getPostalCode(),
                        "country", address.getCountry()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(addressList);
    }
}

