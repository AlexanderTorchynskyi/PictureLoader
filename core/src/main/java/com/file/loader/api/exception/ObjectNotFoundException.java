package com.file.loader.api.exception;

import com.file.loader.utils.ApplicationErrorCodes;

public class ObjectNotFoundException extends RuntimeException {

    private final ObjectType objectType;

    public ObjectNotFoundException(ObjectType objectType) {
        super(String.format("object %s not found ", objectType));
        this.objectType = objectType;

    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public enum ObjectType {
        PICTURE
    }

    public String getErrorCode() {
        return ApplicationErrorCodes.OBJECT_NOT_FOUND_ERROR.getErrorCode();
    }
}
