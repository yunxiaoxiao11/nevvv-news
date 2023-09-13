package com.nevvv.model.common.dtos;

import com.nevvv.model.common.enums.HttpCodeEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class PageResponseResult extends ResponseResult implements Serializable {
    private Integer currentPage;
    private Integer size;
    private Long total;

    public PageResponseResult(Integer currentPage, Integer size, Long total) {
        this.currentPage = currentPage;
        this.size = size;
        this.total = total;
    }

    public PageResponseResult() {

    }

    public static <T> PageResponseResult okResult(Integer currentPage,Integer size,Long total,T data) {
        PageResponseResult pageResponseResult = new PageResponseResult(currentPage, size, total);
        pageResponseResult.setCode(HttpCodeEnum.SUCCESS.getCode());
        pageResponseResult.setErrorMessage(HttpCodeEnum.SUCCESS.getErrorMessage());
        pageResponseResult.setData(data);
        return pageResponseResult;
    }



}
