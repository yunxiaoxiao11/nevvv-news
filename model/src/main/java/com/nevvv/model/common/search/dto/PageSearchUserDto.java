package com.nevvv.model.common.search.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PageSearchUserDto {
    private Integer pageSize;
    private String searchWords;
    private Date minBehotTime;
}
