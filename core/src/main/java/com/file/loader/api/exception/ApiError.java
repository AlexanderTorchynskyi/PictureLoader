package com.file.loader.api.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ApiError {

    @JsonProperty("errorCode")
    private final String errorCode;

    @JsonProperty("errorMessage")
    private final String errorMessage;

    @JsonProperty("detailedErrors")
    private final Map<String, String> detailedErrors;

    public ApiError(String errorCode, String message, Map<String, String> detailedErrors) {
        this.errorCode = errorCode;
        this.errorMessage = message;
        this.detailedErrors = detailedErrors;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Map<String, String> getDetailedErrors() {
        return detailedErrors;
    }

}
