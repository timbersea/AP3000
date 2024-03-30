package com.an.common;

import com.anju.common.core.domain.AjaxResult;
import org.apache.thrift.TApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionController {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionController.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public AjaxResult exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error("server error");
    }

    @ExceptionHandler(value = TApplicationException.class)
    @ResponseBody
    public AjaxResult exceptionHandler(TApplicationException e) {
        return AjaxResult.error(e.getMessage());
    }
}
