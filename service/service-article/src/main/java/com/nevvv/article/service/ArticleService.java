package com.nevvv.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nevvv.model.common.article.dto.ArticleDocDto;
import com.nevvv.model.common.article.dto.ArticleHomeDto;
import com.nevvv.model.common.article.dto.ArticleHotDto;
import com.nevvv.model.common.article.pojo.Article;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.wemedia.dto.WeNews2ArticleDto;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文章信息表，存储已发布的文章 服务类
 * </p>
 *
 * @author
 * @since 2023-05-31
 */
public interface ArticleService extends IService<Article> {

    ResponseResult<List<Article>> loadNewByTag(ArticleHomeDto dto);

    /**
     * 下拉刷新
     * @param dto
     * @return
     */
    ResponseResult<List<Article>> loadNew(ArticleHomeDto dto);

    /**
     * 上滑加载
     * @param dto
     * @return
     */
    ResponseResult<List<Article>> loadMore(ArticleHomeDto dto);

    /**
     * 接收News服务发布的文章信息
     * @param dto
     * @return
     */
    ResponseResult<Long> saveFromNews(WeNews2ArticleDto dto);
    /**
     * 查询所有文章的
     * 该接口是一个内部接口，被search服务远程调用的。
     */
    ResponseResult<List<ArticleDocDto>> findAll();

    ResponseResult<ArticleDocDto> findById(Long id);

    ResponseResult loadBehavior(Map<String, String> map);

    void hotNews();

    ResponseResult<List<ArticleHotDto>> getHotNewsByChannalId(String chanalId);
}
