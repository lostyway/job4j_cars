package ru.job4j.cars.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.exception.NotFoundException;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IService<User, Integer> {
    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        User savedUser = userRepository.create(user);
        if (savedUser == null) {
            throw new NotFoundException("Пользователь не был найден");
        }
        return savedUser;
    }

    @Override
    public boolean update(User user, int id) {
        try {
            user.setId(id);
            userRepository.update(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void delete(Integer id) {
        userRepository.delete(id);
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь '%d' не был найден: ", id)));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAllOrderById();
    }
}
