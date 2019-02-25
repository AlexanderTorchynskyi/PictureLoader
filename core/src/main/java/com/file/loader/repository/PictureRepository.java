package com.file.loader.repository;

import com.file.loader.domain.Picture;
import com.file.loader.model.PictureStatus;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PictureRepository extends MongoRepository<Picture, ObjectId> {

    List<Picture> findFirst50ByPictureStatus(PictureStatus pictureStatus);

}