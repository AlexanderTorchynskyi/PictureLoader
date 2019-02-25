package com.file.loader.utils;

public enum ApplicationErrorCodes {

    INTERNAL_SERVER_ERROR("FL_ERROR_001"),
    DATABASE_ERROR("FL_ERROR_002"),
    OBJECT_NOT_FOUND_ERROR("FL_ERROR_003"),
    BAD_REQUEST_DATA_ERROR("FL_ERROR_004"),
    VALIDATION_ERROR("FL_ERROR_005");


    private String errorCode;

    ApplicationErrorCodes(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
