package whz.pti.eva.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whz.pti.eva.payment.domain.Payment;
import whz.pti.eva.payment.domain.PaymentRepository;

import java.math.BigDecimal;
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    @Override
    public Payment createAccount(Long customerId) {
        Payment payment = new Payment(customerId, BigDecimal.valueOf(100.00), "EUR");
        payment.setCustomerId(customerId);
        payment.setBalance(BigDecimal.valueOf(100));  // Default balance
        payment.setCurrency("EUR");
        return paymentRepository.save(payment);
    }

    @Override
    public boolean processPayment(Long customerId, BigDecimal amount) {
        Payment payment = paymentRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Payment account not found."));

        BigDecimal orderAmount = BigDecimal.valueOf(amount);

        if (payment.getBalance().compareTo(orderAmount) >= 0) {
            payment.setBalance(payment.getBalance().subtract(orderAmount));
            paymentRepository.save(payment);
            return true;
        } else {
            return false;  // Insufficient funds
        }
    }

    @Override
    public Double getBalance(Long customerId) {
        return paymentRepository.findByCustomerId(customerId)
                .map(payment -> payment.getBalance().doubleValue())
                .orElse(0.0);
    }
}
