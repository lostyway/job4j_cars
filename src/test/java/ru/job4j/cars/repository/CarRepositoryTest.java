package ru.job4j.cars.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.Mark;
import ru.job4j.cars.model.Model;
import utils.TestEntityFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CarRepositoryTest extends AbstractRepositoryTest<Car> {
    private CarRepository carRepository;
    private ModelRepository modelRepository;
    private MarkRepository markRepository;
    private EngineRepository engineRepository;

    @BeforeAll
    void setUp() {
        carRepository = new CarRepository(tx);
        modelRepository = new ModelRepository(tx);
        markRepository = new MarkRepository(tx);
        engineRepository = new EngineRepository(tx);
    }


    @Test
    void whenCreateCar() {
        Mark mark = TestEntityFactory.newMark("testMark");
        markRepository.create(mark);
        Model model = TestEntityFactory.newModel("testModel");
        modelRepository.create(model);
        Engine engine = TestEntityFactory.newEngine("testEngine");
        engineRepository.create(engine);
        Car car = TestEntityFactory.newCar("2002", mark, model, engine);
        Car result = carRepository.create(car);
        assertThat(result.getEngine()).isEqualTo(car.getEngine());
        assertThat(result.getMark()).isEqualTo(car.getMark());
        assertThat(result.getModel()).isEqualTo(car.getModel());
        assertThat(result.getYear()).isEqualTo(car.getYear());
    }

    @Test
    void whenFindCarById() {
        Mark mark = TestEntityFactory.newMark("testMark");
        markRepository.create(mark);
        Model model = TestEntityFactory.newModel("testModel");
        modelRepository.create(model);
        Engine engine = TestEntityFactory.newEngine("testEngine");
        engineRepository.create(engine);
        Car car = TestEntityFactory.newCar("2002", mark, model, engine);
        Car expected = carRepository.create(car);
        Optional<Car> resultOpt = carRepository.findById(expected.getId());
        assertThat(resultOpt).isPresent();
        Car result = resultOpt.get();
        assertThat(result.getEngine()).isEqualTo(car.getEngine());
        assertThat(result.getMark()).isEqualTo(car.getMark());
        assertThat(result.getModel()).isEqualTo(car.getModel());
        assertThat(result.getYear()).isEqualTo(car.getYear());
    }

    @Override
    protected String getEntityName() {
        return "Car";
    }
}