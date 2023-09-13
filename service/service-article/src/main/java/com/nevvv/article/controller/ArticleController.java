package com.nevvv.article.controller;


import com.nevvv.article.service.ArticleService;
import com.nevvv.model.common.article.dto.ArticleDocDto;
import com.nevvv.model.common.article.dto.ArticleHomeDto;
import com.nevvv.model.common.article.dto.ArticleHotDto;
import com.nevvv.model.common.article.pojo.Article;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.wemedia.dto.WeNews2ArticleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文章信息表，存储已发布的文章 前端控制器
 * </p>
 *
 * @author
 * @since 2023-05-31
 */
@RestController
@RequestMapping("/api/v1/article")
@Slf4j
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    /**
     * 加载推荐文章
     *
     * @param dto
     * @return
     */
    @PostMapping("/load")
    public ResponseResult<List<Article>> loadNewByTag(@RequestBody ArticleHomeDto dto) {
        log.info("文章展示,参数为:{}", dto);
        return articleService.loadNewByTag(dto);
    }

    /**
     * 上滑加载
     *
     * @param dto
     * @return
     */
    @PostMapping("/loadmore")
    public ResponseResult<List<Article>> loadMore(@RequestBody ArticleHomeDto dto) {
        log.info("上滑加载,参数为:{}", dto);
        return articleService.loadMore(dto);
    }

    /**
     * 下拉刷新
     *
     * @param dto
     * @return
     */
    @PostMapping("/loadnew")
    public ResponseResult<List<Article>> loadNew(@RequestBody ArticleHomeDto dto) {
        log.info("下拉刷新,参数为:{}", dto);
        return articleService.loadNew(dto);
    }

    /**
     * 接收News服务发布文章请求
     *
     * @param dto
     * @return
     */
    @PostMapping("save")
    public ResponseResult save(@RequestBody WeNews2ArticleDto dto) {
        log.info("审核通过后保存,接受的参数为:{}", dto);
        return articleService.saveFromNews(dto);
    }

    /**
     * 查询所有文章的
     * 该接口是一个内部接口，被search服务远程调用的。
     */
    @GetMapping("/findAll")
    public ResponseResult<List<ArticleDocDto>> findAll() {
        log.info("查询所有符合es添加条件的文章...");
        //1.调用service查询所有文章信息
        //2.响应结果
        return articleService.findAll();
    }

    /**
     * 搜索服务远程调用接口,根据id查找文章信息
     *
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    public ResponseResult<ArticleDocDto> getById(@PathVariable Long id) {
        log.info("搜索服务远程调用接口,根据id查找article内容...");
        return articleService.findById(id);
    }

    /**
     * 前端加载文章行为,数据回显
     *
     * @param map
     * @return
     */
    @PostMapping("/load_article_behavior")
    public ResponseResult loadBehavior(@RequestBody Map<String, String> map) {
        log.info("前端加载文章行为,数据回显,接受的参数为:{}", map);
        return articleService.loadBehavior(map);
    }

    /**
     * 前端根据频道id获取热点文章
     *
     * @param chanalId
     * @return
     */
    @GetMapping("/hot/{chanalId}")
    public ResponseResult<List<ArticleHotDto>> getHotNews(@PathVariable String chanalId) {
        return articleService.getHotNewsByChannalId(chanalId);
    }
}