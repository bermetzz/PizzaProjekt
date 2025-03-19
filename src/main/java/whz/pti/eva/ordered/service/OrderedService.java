package whz.pti.eva.ordered.service;

import whz.pti.eva.ordered.domain.OrderDTO;
import whz.pti.eva.ordered.domain.Ordered;

import java.util.List;

public interface OrderedService {
    Ordered placeOrder(String username, Long addressId);
    List<Ordered> getOrdersByCustomer(String username);
    List<OrderDTO> getOrderDTOsByCustomer(String username);
    Double calculateOrderTotal(String username);
}