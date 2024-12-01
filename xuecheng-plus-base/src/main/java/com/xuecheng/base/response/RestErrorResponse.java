package com.xuecheng.base.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Mr.M
 * @version 1.0
 * @description 和前端约定返回的异常信息模型
 * @date 2023/2/12 16:55
 */
@Getter
@Setter
public class RestErrorResponse implements Serializable {

 private Integer errCode;

 private String errMessage;


 public RestErrorResponse(Integer errCode){
  this.errCode= errCode;
 }

 public RestErrorResponse(Integer errCode, String errMessage){
  this.errCode = errCode;
  this.errMessage= errMessage;
 }

 public RestErrorResponse(String errMessage){
  this.errMessage= errMessage;
 }
}
