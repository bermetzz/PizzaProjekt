package whz.pti.eva.ordered.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whz.pti.eva.cart.domain.Cart;
import whz.pti.eva.cart.domain.CartItemDTO;
import whz.pti.eva.cart.domain.CartItemRepository;
import whz.pti.eva.cart.domain.CartRepository;
import whz.pti.eva.cart.service.CartService;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.CustomerRepository;
import whz.pti.eva.customer.domain.DeliveryAddress;
import whz.pti.eva.customer.domain.DeliveryAddressRepository;
import whz.pti.eva.ordered.domain.*;
import whz.pti.eva.payment.service.PaymentService;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderedServiceImpl implements OrderedService {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderedRepository orderedRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final CartService cartService;
    private final PaymentService paymentService;

    @Override
    public Ordered placeOrder(String username, Long addressId) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new IllegalArgumentException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // Calculate total cart value
        double totalOrderAmount = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice().getAmount().doubleValue() * item.getQuantity())
                .sum();

        // Check Payment Service for Balance
        Double currentBalance = paymentService.getBalance(customer.getId());

        if (currentBalance < totalOrderAmount) {
            throw new IllegalArgumentException("Insufficient funds to place the order.");
        }

        // Deduct the Payment
        boolean paymentSuccess = paymentService.processPayment(customer.getId(), BigDecimal.valueOf(totalOrderAmount));
        if (!paymentSuccess) {
            throw new IllegalStateException("Payment processing failed.");
        }

        DeliveryAddress address = deliveryAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        Ordered order = new Ordered();
        order.setCustomer(customer);
        order.setItems(cart.getItems().stream().map(cartItem -> {
            OrderedItem orderedItem = new OrderedItem();
            orderedItem.setPizza(cartItem.getPizza());
            orderedItem.setPrice(cartItem.getPrice());
            orderedItem.setQuantity(cartItem.getQuantity());
            orderedItem.setOrdered(order);
            return orderedItem;
        }).collect(Collectors.toList()));

        orderedRepository.save(order);

        // Clear the cart after placing the order
        cart.getItems().clear();
        cartRepository.save(cart);

        return order;
    }



    @Override
    public List<Ordered> getOrdersByCustomer(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        return orderedRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }

    @Override
    public List<OrderDTO> getOrderDTOsByCustomer(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        List<Ordered> orders = orderedRepository.findByCustomerOrderByCreatedAtDesc(customer);

        return orders.stream().map(this::toOrderDTO).collect(Collectors.toList());
    }

    private OrderDTO toOrderDTO(Ordered order) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(this::toOrderItemDTO)
                .collect(Collectors.toList());

        double totalAmount = itemDTOs.stream()
                .mapToDouble(OrderItemDTO::getTotalPrice)
                .sum();

        return new OrderDTO(
                order.getId(),
                order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                itemDTOs,
                totalAmount
        );
    }

    private OrderItemDTO toOrderItemDTO(OrderedItem item) {
        double totalPrice = item.getQuantity() * item.getPrice().getAmount().doubleValue();
        return new OrderItemDTO(
                item.getPizza().getName(),
                item.getPizza().getImagePath(),
                item.getQuantity(),
                item.getPrice().getAmount().doubleValue(),
                totalPrice
        );
    }
    @Override
    public Double calculateOrderTotal(String username) {
        List<CartItemDTO> cartItems = cartService.getCart(username);

        // Суммируем стоимость всех товаров в корзине
        return cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
    }
}
