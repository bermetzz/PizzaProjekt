package whz.pti.eva.pizza.service;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import whz.pti.eva.pizza.domain.Pizza;
import whz.pti.eva.pizza.domain.PizzaDTO;
import whz.pti.eva.pizza.domain.PizzaRepository;
import whz.pti.eva.pizza.domain.Price;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class PizzaServiceImpl implements PizzaService {

    private final PizzaRepository pizzaRepository;

    @Override
    public List<PizzaDTO> getAllPizzas() {
        return pizzaRepository.findAll().stream().map(this::toDTO).toList();
    }

    private PizzaDTO toDTO(Pizza pizza) {
        List<String> sortedPrices = pizza.getPrices().stream()
                .sorted(Comparator.comparing(price -> price.getSize().ordinal()))
                .map(Price::getAmount)
                .map(price->price.stripTrailingZeros().toPlainString())
                .toList();
        return new PizzaDTO(pizza.getId(), pizza.getName(), pizza.getImagePath(), sortedPrices);
    }
}