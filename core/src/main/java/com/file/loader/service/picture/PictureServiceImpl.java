package com.file.loader.service.picture;

import com.file.loader.api.exception.ObjectNotFoundException;
import com.file.loader.api.exception.StorageException;
import com.file.loader.config.StorageConfiguration;
import com.file.loader.domain.Picture;
import com.file.loader.model.PictureStatus;
import com.file.loader.repository.PictureRepository;
import org.bson.types.ObjectId;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PictureServiceImpl implements PictureService {

    private final PictureRepository pictureRepository;
    private final Path rootLocation;

    public PictureServiceImpl(PictureRepository pictureRepository, StorageConfiguration properties) {
        this.pictureRepository = pictureRepository;
        this.rootLocation = Paths.get(properties.getUploadDir());
        createDirectoryIfNotExist();
    }

    @Override
    public void store(MultipartFile file, String callBackUrl) {
        String filename = validateFile(file);
        try (InputStream inputStream = file.getInputStream()) {

            UUID uuid = saveToDb(file, callBackUrl);
            createDirectories(uuid);
            Files.copy(inputStream, this.rootLocation.resolve(uuid.toString()).resolve("row").resolve(file.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, StorageException.ObjectType.PICTURE);
        }
    }

    @Override
    public Resource loadAsResource(ObjectId imageId, String size) {
        try {
            Path file = getPathToFile(imageId, size);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ObjectNotFoundException(ObjectNotFoundException.ObjectType.PICTURE);
            }
        } catch (MalformedURLException e) {
            throw new ObjectNotFoundException(ObjectNotFoundException.ObjectType.PICTURE);
        }
    }

    @Override
    public void delete(ObjectId imageId) {
        Optional<Picture> pictureOptional = pictureRepository.findById(imageId);
        if (pictureOptional.isPresent()) {
            Path pathToDelete = Paths.get(pictureOptional.get().getPath());
            FileSystemUtils.deleteRecursively(pathToDelete.toFile());
            pictureRepository.deleteById(imageId);
        } else {
            throw new ObjectNotFoundException(ObjectNotFoundException.ObjectType.PICTURE);
        }
    }

    @Override
    public void updateMany(List<Picture> pictures) {
        pictureRepository.saveAll(pictures);
    }

    @Override
    public List<Picture> findByStatus(PictureStatus pictureStatus) {
        return pictureRepository.findFirst50ByPictureStatus(pictureStatus);
    }

    private Path getPathToFile(ObjectId imageId, String size) {
        Optional<Picture> pictureOptional = pictureRepository.findById(imageId);
        if (pictureOptional.isPresent()) {
            return Paths.get(pictureOptional.get().getPath()).resolve(size).resolve(pictureOptional.get().getPictureName());
        } else {
            throw new ObjectNotFoundException(ObjectNotFoundException.ObjectType.PICTURE);
        }
    }

    @Override
    public void updatePicture(Picture picture) {
        pictureRepository.save(picture);
    }

    private void createDirectories(UUID uuid) throws IOException {
        Files.createDirectories(rootLocation.resolve(uuid.toString()).resolve("row"));
    }

    private String validateFile(MultipartFile file) {

        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file " + filename, StorageException.ObjectType.PICTURE);
        }
        if (filename.contains("..")) {
            throw new StorageException(
                    "Cannot store file with relative path outside current directory " + filename, StorageException.ObjectType.PICTURE);
        }
        return filename;
    }

    private UUID saveToDb(MultipartFile file, String callbackUrl) {
        UUID uuid = UUID.randomUUID();

        Picture picture = new Picture();
        picture.setPictureName(file.getOriginalFilename());
        picture.setCallbackUrl(callbackUrl);
        picture.setPath((rootLocation.resolve(uuid.toString()).toString()));
        picture.setCreatedDate(LocalDateTime.now());
        picture.setUpdatedDate(LocalDateTime.now());
        picture.setPictureStatus(PictureStatus.SET_UP);
        picture.setFileFormat(obtainImageFormat(file.getContentType()));
        pictureRepository.save(picture);

        return uuid;
    }


    private String obtainImageFormat(String contentType) {
        return contentType.replaceAll("image/", "");
    }

    private void createDirectoryIfNotExist() {
        if (Files.notExists(rootLocation)) {
            try {
                Files.createDirectories(rootLocation);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
