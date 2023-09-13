package com.nevvv.model.common.dtos;

import com.nevvv.model.common.enums.HttpCodeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果的实体类
 * @param <T>
 */
@Data
public class ResponseResult<T> implements Serializable {

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String errorMessage;

    /**
     * 响应数据
     */
    private T data;

    public ResponseResult() {

    }

    public ResponseResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.errorMessage = msg;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.errorMessage = msg;
    }

    public static ResponseResult errorResult(HttpCodeEnum httpCodeEnum) {
        return new ResponseResult(httpCodeEnum.getCode(), httpCodeEnum.getErrorMessage());
    }

    public static  ResponseResult okResult() {
        return new ResponseResult(HttpCodeEnum.SUCCESS.getCode(), HttpCodeEnum.SUCCESS.getErrorMessage(), HttpCodeEnum.SUCCESS);
    }

    public static <T> ResponseResult okResult(T data) {
        return new ResponseResult(HttpCodeEnum.SUCCESS.getCode(), HttpCodeEnum.SUCCESS.getErrorMessage(),data);
    }

}
