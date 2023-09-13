package com.nevvv.model.common.article.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ArticleDocDto {
    private String id;
    private Date publishTime;
    private Integer layout;
    private String images;
    private String staticUrl;
    private Long authorId;
    private String authorName;
    private String title;
    private String content;
    private String labels;
}