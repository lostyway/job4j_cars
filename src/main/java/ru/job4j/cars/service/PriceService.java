package ru.job4j.cars.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.exception.NotFoundException;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.repository.PriceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceService implements IService<PriceHistory, Integer>{
    private final PriceRepository priceRepository;

    @Override
    public PriceHistory save(PriceHistory priceHistory) {
        PriceHistory savedHistory = priceRepository.create(priceHistory);
        if (savedHistory == null) {
            throw new NotFoundException("Пользователь не был найден");
        }
        return savedHistory;
    }

    @Override
    public boolean update(PriceHistory priceHistory, int id) {
        try {
            priceHistory.setId(id);
            priceRepository.update(priceHistory);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void delete(Integer id) {
        priceRepository.delete(id);
    }

    @Override
    public PriceHistory findById(Integer id) {
        return priceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь '%d' не был найден: ", id)));
    }

    @Override
    public List<PriceHistory> findAll() {
        return priceRepository.findAllOrderById();
    }
}
