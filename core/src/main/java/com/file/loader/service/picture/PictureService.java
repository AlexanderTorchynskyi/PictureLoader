package com.file.loader.service.picture;

import com.file.loader.domain.Picture;
import com.file.loader.model.PictureStatus;
import org.bson.types.ObjectId;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PictureService {

    void store(MultipartFile file, String callbackUrl);

    void updatePicture(Picture picture);

    Resource loadAsResource(ObjectId imageId, String size);

    List<Picture> findByStatus(PictureStatus pictureStatus);

    void delete(ObjectId imageId);
}
