package com.file.loader.domain;


import com.file.loader.model.PictureStatus;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "photo_metadata")
public class Picture {

    @Id
    @Field("id")
    private ObjectId id;

    @Field("path")
    private String path;

    @Field("create_date")
    private LocalDateTime createdDate;

    @Field("update_date")
    private LocalDateTime updatedDate;

    @Field("picture_name")
    private String pictureName;

    @Field("callback_url")
    private String callbackUrl;

    @Field("status")
    private PictureStatus pictureStatus;

    @Field("send_counter")
    private long sendCounter;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public PictureStatus getPictureStatus() {
        return pictureStatus;
    }

    public void setPictureStatus(PictureStatus pictureStatus) {
        this.pictureStatus = pictureStatus;
    }

    public long getSendCounter() {
        return sendCounter;
    }

    public void setSendCounter(long sendCounter) {
        this.sendCounter = sendCounter;
    }
}
