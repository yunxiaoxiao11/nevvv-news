package com.nevvv.search.listener;

import com.alibaba.fastjson.JSON;
import com.nevvv.model.common.article.dto.ArticleDocDto;
import com.nevvv.model.common.article.pojo.ArticleConfig;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.enums.HttpCodeEnum;
import com.nevvv.model.common.search.pojo.ArticleDoc;
import feign.article.ArticleFeign;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

import java.io.IOException;

@Component
@CanalTable("ap_article_config")
@Slf4j
public class CanalListener implements EntryHandler<ArticleConfig> {
    @Autowired
    private ArticleFeign articleFeign;
    @Autowired
    private RestHighLevelClient highLevelClient;

    @Override
    public void insert(ArticleConfig articleConfig) {
        log.info("远程调用article服务获取文章信息...");
        ResponseResult<ArticleDocDto> articleDocDto = articleFeign.getById(articleConfig.getArticleId());
        log.info("发布文章,同步到ES...");
        syncMysql2Es(articleConfig, articleDocDto);
    }

    @Override
    public void update(ArticleConfig before, ArticleConfig after) {
        log.info("远程调用article服务获取文章信息...");
        ResponseResult<ArticleDocDto> articleDocDto = articleFeign.getById(after.getArticleId());
        if (after.getIsDown()) {
            log.info("下架文章,同步到ES...");
            DeleteRequest deleteRequest = new DeleteRequest("app_info_article");
            deleteRequest.id(String.valueOf(after.getArticleId()));
            try {
                highLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                log.info("删除es中数据失败!");
                throw new RuntimeException(e);
            }
        } else {
            log.info("上架文章,同步到ES...");
            syncMysql2Es(after, articleDocDto);
        }
    }

    private void syncMysql2Es(ArticleConfig articleConfig, ResponseResult<ArticleDocDto> articleDocDto) {
        if (articleDocDto.getCode() == HttpCodeEnum.SUCCESS.getCode()) {
            ArticleDoc articleDoc = new ArticleDoc(articleDocDto.getData());
            IndexRequest indexRequest = new IndexRequest("app_info_article");
            indexRequest.id(String.valueOf(articleConfig.getArticleId()));
            indexRequest.source(JSON.toJSONString(articleDoc), XContentType.JSON);
            try {
                highLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                log.info("新增数据到es失败!");
                throw new RuntimeException(e);
            }
        }
    }
}