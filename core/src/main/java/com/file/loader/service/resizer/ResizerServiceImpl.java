package com.file.loader.service.resizer;

import com.file.loader.config.StorageConfiguration;
import com.file.loader.domain.Picture;
import com.file.loader.model.PictureStatus;
import com.file.loader.service.picture.PictureService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class ResizerServiceImpl implements ResizerService {

    private final PictureService pictureService;
    private final Path rootLocation;

    public ResizerServiceImpl(PictureService pictureService, StorageConfiguration properties) {
        this.pictureService = pictureService;
        this.rootLocation = Paths.get(properties.getUploadDir());
    }

    @Override
    @Scheduled(fixedDelay = 1000 * 60)
    public void resizeImage() {
        try {
            List<Picture> pictureList = pictureService.findByStatus(PictureStatus.SET_UP);
            changePictureStatus(pictureList, PictureStatus.RESIZING);

            for (Picture picture : pictureList) {
                createDirectories(picture.getPath().replace("uploads/", ""));
                createAndSaveNewSizeImages(400, 300, picture, "/small/");
                createAndSaveNewSizeImages(900, 600, picture, "/medium/");
                createAndSaveNewSizeImages(1024, 800, picture, "/large/");
            }
            changePictureStatus(pictureList, PictureStatus.RESIZED);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createAndSaveNewSizeImages(int weidth, int height, Picture picture, String folderName) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File(picture.getPath() + "/row/" + picture.getPictureName()));

        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

        BufferedImage imageBig = resize(originalImage, weidth, height, type);
        ImageIO.write(imageBig, picture.getFileFormat(), new File(picture.getPath() + folderName + picture.getPictureName()));
    }

    private BufferedImage crop(BufferedImage originalImage, double customWidth, double customHight) {

        int x, y, width, height;

        double imageWidth = originalImage.getWidth();
        double imageHeight = originalImage.getHeight();

        double koef = customHight / customWidth;

        if (imageWidth > imageHeight) {
            width = (int) (imageWidth * koef);
            height = (int) imageHeight;
            x = (int) (imageWidth - width) / 2;
            y = 0;
        } else {
            width = (int) imageWidth;
            height = (int) (imageWidth * koef);
            x = 0;
            y = (int) (imageHeight - height) / 2;
        }
        return originalImage.getSubimage(x, y, width, height);
    }

    private BufferedImage resize(BufferedImage originalImage, int width, int height, int type) {
        BufferedImage cropedImage = crop(originalImage, width, height);

        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(cropedImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

    private void createDirectories(String location) throws IOException {
        Files.createDirectories(rootLocation.resolve(location + "/small"));
        Files.createDirectories(rootLocation.resolve(location + "/medium"));
        Files.createDirectories(rootLocation.resolve(location + "/large"));
    }

    private void changePictureStatus(List<Picture> pictures, PictureStatus status) {
        pictures.forEach(picture -> {
            picture.setPictureStatus(status);
        });

        pictureService.updateMany(pictures);
    }
}
