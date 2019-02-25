package com.file.loader.api.rest.exception;

import com.file.loader.api.exception.ObjectNotFoundException;
import com.file.loader.api.rest.v1.dto.ApiError;
import com.file.loader.utils.ApplicationErrorCodes;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationError(MethodArgumentNotValidException e) {

        Map<String, String> detailedError = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage,
                        (o1, o2) -> o1));
        com.file.loader.api.exception.ApiError apiError = new com.file.loader.api.exception.ApiError(ApplicationErrorCodes.VALIDATION_ERROR.getErrorCode(), e.getMessage(),
                detailedError);
        return new ResponseEntity<>(new ApiError(apiError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleValidationError(ConstraintViolationException e) {
        log.error(String.format("Error: %s / %s", ApplicationErrorCodes.VALIDATION_ERROR.getErrorCode(), e.getMessage()));

        Map<String, String> detailedError = e.getConstraintViolations().stream()
                .collect(Collectors.toMap(error -> ((PathImpl) error.getPropertyPath()).getLeafNode().getName(),
                        ConstraintViolation::getMessage, (o1, o2) -> o1));
        com.file.loader.api.exception.ApiError apiError = new com.file.loader.api.exception.ApiError(ApplicationErrorCodes.VALIDATION_ERROR.getErrorCode(), e.getMessage(),
                detailedError);
        return new ResponseEntity<>(new ApiError(apiError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(final ObjectNotFoundException e) {
        log.error(String.format("Error: %s / %s", e.getErrorCode(), e.getMessage()));

        com.file.loader.api.exception.ApiError apiError = new com.file.loader.api.exception.ApiError(e.getErrorCode(), e.getMessage(),
                Collections.singletonMap("type", String.valueOf(e.getObjectType())));
        return new ResponseEntity<>(new ApiError(apiError), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleInternalException(final Exception e) {
        log.error(String.format("Error: %s / %s", ApplicationErrorCodes.INTERNAL_SERVER_ERROR.getErrorCode(), e.getMessage()), e);

        com.file.loader.api.exception.ApiError apiError = new com.file.loader.api.exception.ApiError(ApplicationErrorCodes.INTERNAL_SERVER_ERROR.getErrorCode(), e.getMessage(), Collections.emptyMap());
        return new ResponseEntity<>(new ApiError(apiError), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping("/errors-directory/{errorCode}")
    public ResponseEntity getError(@PathVariable("errorCode") String errorCode) {
        return ResponseEntity.notFound().build();
    }
}
