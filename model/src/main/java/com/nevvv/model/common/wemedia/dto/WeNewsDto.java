package com.nevvv.model.common.wemedia.dto;

import lombok.Data;

import java.util.Date;

@Data
public class WeNewsDto {
    private Integer id;
    private String title;
    private Integer type;
    private String labels;
    private Date publishTime;
    private Integer channelId;
    private String images;
    private Integer status;
    private String content;
}
