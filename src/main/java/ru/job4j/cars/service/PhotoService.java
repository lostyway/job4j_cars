package ru.job4j.cars.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.PhotoDto;
import ru.job4j.cars.exception.NotFoundException;
import ru.job4j.cars.model.Photo;
import ru.job4j.cars.repository.PhotoRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.nio.file.Files.createDirectories;

@Service
public class PhotoService implements IPhotoService {
    private final PhotoRepository photoRepository;
    private String storageDirectory;

    public PhotoService(PhotoRepository photoRepository, @Value("${photo.directory}") String storageDirectory) throws IOException {
        this.photoRepository = photoRepository;
        this.storageDirectory = storageDirectory;
        createDirectories(Path.of(storageDirectory));
    }

    @Override
    public Photo save(PhotoDto photoDto) {
        Path path = Path.of(storageDirectory, photoDto.getName());
        writeAllBytes(path, photoDto.getContent());
        return photoRepository.save(new Photo(photoDto.getName(), path.toString()));
    }

    private void writeAllBytes(Path path, byte[] content) {
        try {
            Files.write(path, content);
        } catch (IOException e) {
            throw new RuntimeException("Не получилось сохранить файл: " + path, e);
        }
    }

    public PhotoDto convertToPhotoDto(MultipartFile file) {
        try {
            return new PhotoDto(file.getOriginalFilename(), file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла", e);
        }
    }


    private byte[] readAllAsBytes(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (Exception e) {
            throw new RuntimeException("Не получилось прочитать файл: " + path, e);
        }
    }

    @Override
    public Optional<PhotoDto> getPhotoById(int id) {
        Optional<Photo> photoOptional = photoRepository.findById(id);
        if (photoOptional.isEmpty()) {
            return Optional.empty();
        }
        var content = readAllAsBytes(photoOptional.get().getPath());
        return Optional.of(new PhotoDto(photoOptional.get().getName(), content));
    }

    @Override
    public void deleteById(int id) {
        var photoOptional = photoRepository.findById(id);
        if (photoOptional.isEmpty()) {
            throw new NotFoundException("Не удалось удалить фото: " + id);
        }
        deletePhoto(Path.of(photoOptional.get().getPath()));
        photoRepository.deleteById(id);
    }

    private void deletePhoto(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось удалить фото: " + path, e);
        }
    }

    @Override
    public List<PhotoDto> getAll() {
        List<Photo> Photos = photoRepository.findAll();
        List<PhotoDto> result = new ArrayList<>();
        for (Photo photo : Photos) {
            byte[] content = readAllAsBytes(photo.getPath());
            result.add(new PhotoDto(photo.getName(), content));
        }
        return result;
    }
}
