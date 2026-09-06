package com.an.common;

import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
public class GlobalExceptionController {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionController.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        String message = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
        return ApiResponse.fail(500, message);
    }
}
