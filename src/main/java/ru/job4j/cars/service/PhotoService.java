package ru.job4j.cars.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.PhotoDto;
import ru.job4j.cars.exception.NotFoundException;
import ru.job4j.cars.model.Photo;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.PhotoRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.nio.file.Files.createDirectories;

@Slf4j
@Service
public class PhotoService implements IPhotoService {
    private final PhotoRepository photoRepository;
    private final PostService postService;
    private String storageDirectory;

    public PhotoService(PhotoRepository photoRepository, @Value("${photo.directory}") String storageDirectory, PostService postService) throws IOException {
        this.photoRepository = photoRepository;
        this.storageDirectory = storageDirectory;
        this.postService = postService;

        createDirectories(Path.of(storageDirectory));
    }

    @Override
    public Photo save(PhotoDto photoDto, Post post) {
        try {
            String nameUuid = UUID.randomUUID() + "_" + photoDto.getName();
            Path path = Path.of(storageDirectory, nameUuid);

            writeAllBytes(path, photoDto.getContent());

            Photo newPhoto = new Photo(nameUuid, path.toString(), post);
            photoRepository.save(newPhoto);
            post.getPhotos().add(newPhoto);
            return newPhoto;
        } catch (Exception e) {
            throw new RuntimeException("Не получилось сохранить файл: ", e);
        }
    }

    private void writeAllBytes(Path path, byte[] content) {
        try {
            Files.write(path, content);
        } catch (IOException e) {
            throw new RuntimeException("Не получилось сохранить файл: " + path, e);
        }
    }

    public void savePhotos(Post post, List<MultipartFile> photos) throws IOException {
        for (MultipartFile photo : photos) {
            if (!photo.isEmpty()) {
                PhotoDto photoDto = new PhotoDto(photo.getOriginalFilename(), photo.getBytes());
                save(photoDto, post);
            }
        }
    }

    public void checkAndDeletePhotosInPost(List<MultipartFile> photos, Post postFromBase) {
        try {
            Post oldPost = postService.findById(postFromBase.getId());
            Set<Photo> oldPhotos = oldPost.getPhotos();
            boolean hasNewPhotos = photos != null && photos.stream().anyMatch(p -> !p.isEmpty());
            if (hasNewPhotos) {
                for (Photo oldPhoto : oldPhotos) {
                    deleteById(oldPhoto.getId());
                }
                savePhotos(postFromBase, photos);
            } else {
                postFromBase.setPhotos(oldPhotos);
            }
        } catch (Exception e) {
            throw new RuntimeException("Не удалось заменить фотографии");
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

    public void deleteAllPhotosInPost(Post currentPost) {
        currentPost.getPhotos().forEach(photo -> deleteById(photo.getId()));
        //photoRepository.deleteAllPhotosInPost(currentPost);
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
