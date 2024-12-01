package com.xuecheng.base.model;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * @program: xuecheng-plus-project
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-30 11:11
 **/
@AllArgsConstructor
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    private T data;

    public static <T> Result<T> success() {
        return new Result<>(HttpStatus.OK.value(), null, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(HttpStatus.OK.value(), null, data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(HttpStatus.OK.value(), message, data);
    }

    public static <T> Result<T> error(Integer code) {
        return new Result<>(code, null, null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }

}
