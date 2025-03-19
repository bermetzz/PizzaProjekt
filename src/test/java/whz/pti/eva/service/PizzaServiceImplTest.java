package whz.pti.eva.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import whz.pti.eva.pizza.domain.*;
import whz.pti.eva.pizza.service.PizzaServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PizzaServiceImplTest {

    @Mock
    private PizzaRepository pizzaRepository;

    private PizzaServiceImpl pizzaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pizzaService = new PizzaServiceImpl(pizzaRepository);
    }

    @Test
    void testGetAllPizzas() {
        Pizza pizza1 = new Pizza();
        pizza1.setId(1L);
        pizza1.setName("Margherita");
        pizza1.setImagePath("/images/margherita.jpg");
        Price price1 = new Price();
        price1.setAmount(BigDecimal.valueOf(5.99));
        price1.setSize(PizzaSize.SMALL);
        Price price2 = new Price();
        price2.setAmount(BigDecimal.valueOf(7.99));
        price2.setSize(PizzaSize.MEDIUM);
        pizza1.setPrices(List.of(price2, price1));

        Pizza pizza2 = new Pizza();
        pizza2.setId(2L);
        pizza2.setName("Pepperoni");
        pizza2.setImagePath("/images/pepperoni.jpg");
        Price price3 = new Price();
        price3.setAmount(BigDecimal.valueOf(8.99));
        price3.setSize(PizzaSize.LARGE);
        pizza2.setPrices(List.of(price3));

        when(pizzaRepository.findAll()).thenReturn(List.of(pizza1, pizza2));

        List<PizzaDTO> result = pizzaService.getAllPizzas();

        assertEquals(2, result.size());

        PizzaDTO dto1 = result.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("Margherita", dto1.getName());
        assertEquals("/images/margherita.jpg", dto1.getImagePath());
        assertEquals(List.of("5.99", "7.99"), dto1.getPrices());

        PizzaDTO dto2 = result.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("Pepperoni", dto2.getName());
        assertEquals("/images/pepperoni.jpg", dto2.getImagePath());
        assertEquals(List.of("8.99"), dto2.getPrices());
    }
}
