package com.xuecheng.base.handler;

import cn.hutool.core.util.StrUtil;
import com.xuecheng.base.CommonError;
import com.xuecheng.base.model.Result;
import com.xuecheng.base.response.RestErrorResponse;
import com.xuecheng.base.response.XueChengPlusException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: xuecheng-plus-project
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-12-01 12:27
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(XueChengPlusException.class)
    public RestErrorResponse handlerCustomException(XueChengPlusException ex){
        return new RestErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public RestErrorResponse handlerException(Exception ex){
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
    }

    @ExceptionHandler(BindException.class)
    public RestErrorResponse handlerValidation(BindException ex) {
        List<String> errors = new ArrayList<>();
        ex.getFieldErrors().stream().forEach(e -> {
            errors.add(e.getDefaultMessage());
        });
        return new RestErrorResponse(StringUtils.join(errors, ","));
    }

}
