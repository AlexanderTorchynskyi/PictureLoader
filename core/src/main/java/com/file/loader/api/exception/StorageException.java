package com.file.loader.api.exception;

import com.file.loader.utils.ApplicationErrorCodes;

public class StorageException extends RuntimeException {

    private final ObjectType objectType;

    public StorageException(String message, ObjectType objectType) {
        super(String.format(message, objectType));
        this.objectType = objectType;
    }


    public ObjectType getObjectType() {
        return objectType;
    }

    public enum ObjectType {
        PICTURE
    }

    public String getErrorCode() {
        return ApplicationErrorCodes.BAD_REQUEST_DATA_ERROR.getErrorCode();
    }

}
