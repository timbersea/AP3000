package com.an.common;

import com.anju.common.core.domain.AjaxResult;
import org.apache.thrift.TApplicationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionController {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public AjaxResult exceptionHandler(Exception e) {
        return AjaxResult.error("server error");
    }

    @ExceptionHandler(value = TApplicationException.class)
    @ResponseBody
    public AjaxResult exceptionHandler(TApplicationException e) {
        return AjaxResult.error(e.getMessage());
    }
}
