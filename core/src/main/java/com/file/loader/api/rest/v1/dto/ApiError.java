package com.file.loader.api.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiError {

    @JsonProperty("apiError")
    private final com.file.loader.api.exception.ApiError apiError;

    public ApiError(com.file.loader.api.exception.ApiError apiError) {
        this.apiError = apiError;
    }

    public com.file.loader.api.exception.ApiError getApiError() {
        return apiError;
    }
}
