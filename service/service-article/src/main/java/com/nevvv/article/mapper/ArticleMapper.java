package com.nevvv.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nevvv.model.common.article.dto.ArticleDocDto;
import com.nevvv.model.common.article.dto.ArticleHotDto;
import com.nevvv.model.common.article.pojo.Article;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 文章信息表，存储已发布的文章 Mapper 接口
 * </p>
 *
 * @author
 * @since 2023-05-31
 */
public interface ArticleMapper extends BaseMapper<Article> {

    List<Article> loadNewByTag(String tag, Integer size);

    List<Article> loadMore(String tag, Integer size, Date minBehotTime);

    List<Article> loadNew(String tag, Integer size, Date maxBehotTime);

    List<ArticleDocDto> findAll();

    ArticleDocDto findById(Long id);

    List<ArticleHotDto> hotNews(Date date);
}
