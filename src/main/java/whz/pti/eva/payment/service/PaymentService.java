package whz.pti.eva.payment.service;

import whz.pti.eva.payment.domain.Payment;

import java.math.BigDecimal;

public interface PaymentService {
    Payment createAccount(Long customerId);
    boolean processPayment(Long customerId, BigDecimal amount);
    Double getBalance(Long customerId);
}