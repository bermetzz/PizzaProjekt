package whz.pti.eva.payment.boundary;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import whz.pti.eva.payment.domain.Payment;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
public class MockPaymentController {

    @PostMapping("/create/{customerId}")
    public ResponseEntity<Payment> createAccount(@PathVariable Long customerId) {
        Payment payment = new Payment(customerId, BigDecimal.valueOf(100.00), "EUR");
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/process/{customerId}")
    public ResponseEntity<Boolean> processPayment(@PathVariable Long customerId, @RequestParam BigDecimal amount) {
        // Assume every customer has sufficient funds in the mock
        return ResponseEntity.ok(true);
    }

    @GetMapping("/balance/{customerId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long customerId) {
        // Return a fixed balance for testing
        return ResponseEntity.ok(BigDecimal.valueOf(100.00));
    }
}

