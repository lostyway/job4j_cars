package ru.job4j.cars.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.exception.NotFoundException;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.repository.CarRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService implements IService<Car, Integer> {
    private final CarRepository carRepository;

    @Override
    public Car findById(Integer id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Машина не была найдена: " + id));
    }

    @Override
    public Car save(Car car) {
        Car resultOfCreation = carRepository.create(car);
        if (resultOfCreation == null) {
            throw new IllegalArgumentException("Машина не была сохранена");
        }
        return resultOfCreation;
    }

    @Override
    public boolean updatePostAndCar(Car car, int id) {
        try {
            car.setId(id);
            carRepository.update(car);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            carRepository.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении автомобиля " + id);
        }
    }

    @Override
    public List<Car> findAll() {
        var list = carRepository.findAllOrderById();
        if (list.isEmpty()) {
            throw new NotFoundException("Машины не были найдены");
        }
        return list;
    }
}
