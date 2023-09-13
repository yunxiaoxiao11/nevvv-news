package com.nevvv.model.common.search.pojo;

import com.nevvv.model.common.article.dto.ArticleDocDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

// 索引库中文档类型
@Data
@NoArgsConstructor
public class ArticleDoc {
    private String id;
    private Date publishTime;
    private Integer layout;
    private String images;
    private String staticUrl;
    private Long authorId;
    private String authorName;
    private String title;
    private String content;
    private List<String> labels;

    public ArticleDoc(ArticleDocDto articleDocDto) {
        this.id = articleDocDto.getId();
        this.publishTime = articleDocDto.getPublishTime();
        this.layout = articleDocDto.getLayout();
        this.images = articleDocDto.getImages();
        this.staticUrl = articleDocDto.getStaticUrl();
        this.authorId = articleDocDto.getAuthorId();
        this.authorName = articleDocDto.getAuthorName();
        this.title = articleDocDto.getTitle();
        this.content = articleDocDto.getContent();
        String labels = articleDocDto.getLabels();
        if (labels != null) {
            String[] split = labels.split(",");
            this.labels = Arrays.asList(split);
        }
    }
}