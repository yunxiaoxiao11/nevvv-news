package com.nevvv.model.common.article.dto;

import com.nevvv.model.common.article.pojo.Article;
import lombok.Data;

@Data
public class ArticleHotDto extends Article {
    private Integer score;
}
