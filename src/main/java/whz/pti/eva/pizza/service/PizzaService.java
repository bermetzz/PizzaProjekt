package whz.pti.eva.pizza.service;

import whz.pti.eva.pizza.domain.Pizza;
import whz.pti.eva.pizza.domain.PizzaDTO;

import java.util.List;

public interface PizzaService {
    List<PizzaDTO> getAllPizzas();
}