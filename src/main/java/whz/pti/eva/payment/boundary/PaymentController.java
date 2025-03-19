package whz.pti.eva.payment.boundary;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import whz.pti.eva.payment.domain.Payment;
import whz.pti.eva.payment.service.PaymentService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create/{customerId}")
    public ResponseEntity<Payment> createPayment(@PathVariable Long customerId) {
        Payment payment = paymentService.createAccount(customerId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/process/{customerId}")
    public ResponseEntity<Boolean> processPayment(@PathVariable Long customerId,
                                                  @RequestParam Double amount) {
        boolean success = paymentService.processPayment(customerId, BigDecimal.valueOf(amount));
        return ResponseEntity.ok(success);
    }

    @GetMapping("/balance/{customerId}")
    public ResponseEntity<Double> getBalance(@PathVariable Long customerId) {
        Double balance = paymentService.getBalance(customerId);
        return ResponseEntity.ok(balance);
    }
}
