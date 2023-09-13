package com.nevvv.model.common.article.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ArticleHomeDto {
    Date maxBehotTime;  // 最大时间
    Date minBehotTime;  // 最小时间
    Integer size;   // 分页size
    String tag; // 频道ID
    Integer index;
    Integer loaddir;
}