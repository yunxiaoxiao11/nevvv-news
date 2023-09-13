package com.nevvv.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.enums.HttpCodeEnum;
import com.nevvv.model.common.history.pojo.ApUserSearch;
import com.nevvv.model.common.search.pojo.ArticleDoc;
import com.nevvv.model.common.search.dto.PageSearchUserDto;
import com.nevvv.search.service.SearchService;
import com.nevvv.utils.shareData.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {
    @Autowired
    private RestHighLevelClient highLevelClient;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseResult search(PageSearchUserDto pageSearchUserDto) {
        //非空校验
        if (StringUtils.isBlank(pageSearchUserDto.getSearchWords())) {
            return ResponseResult.errorResult(HttpCodeEnum.PARAM_INVALID);
        }
        //有查询词
        saveHistory2Mongo(pageSearchUserDto.getSearchWords());
        SearchRequest searchRequest = new SearchRequest("app_info_article");
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.multiMatchQuery(pageSearchUserDto.getSearchWords(), "title", "content"));
        //排序,分页,高亮
        searchRequest.source()
                .query(queryBuilder)
                .sort("publishTime", SortOrder.DESC)
                .from(0)
                .size(10)
                .highlighter(new HighlightBuilder().requireFieldMatch(false)
                        .field("title")
                        .preTags("<font style='color: red; font-size: inherit;'>")
                        .postTags("</font>"));
        if (pageSearchUserDto.getMinBehotTime() != null && pageSearchUserDto.getMinBehotTime()
                .before(new Date())) {
            queryBuilder.must(QueryBuilders.rangeQuery("publishTime")
                    .lt(pageSearchUserDto.getMinBehotTime()));
        }
        SearchResponse search;
        try {
            search = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            return ResponseResult.errorResult(HttpCodeEnum.SERVER_ERROR);
        }
        //解析查询结果
        SearchHit[] hits = search.getHits()
                .getHits();
        if (hits.length == 0) {
            return ResponseResult.okResult(Collections.EMPTY_LIST);
        } else {
            List<ArticleDoc> docList = new ArrayList<>();
            for (SearchHit hit : hits) {
                String sourceAsString = hit.getSourceAsString();
                ArticleDoc articleDoc = JSON.parseObject(sourceAsString, ArticleDoc.class);
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                //高亮关键词存在校验
                if (highlightFields != null) {
                    HighlightField title = highlightFields.get("title");
                    if (title != null) {
                        articleDoc.setTitle(title
                                .getFragments()[0].string());
                    }
                }
                docList.add(articleDoc);
            }
            return ResponseResult.okResult(docList);
        }
    }

    @Async
    public void saveHistory2Mongo(String searchWords) {
        log.info("保存或更新登录用户搜索关键词到mongoDb");
        ApUserSearch apUserSearch = new ApUserSearch();
        apUserSearch.setKeyword(searchWords);
        apUserSearch.setCreatedTime(new Date());
        if (BaseContext.getThreadId() != null) {
            log.info("登录用户身份,需要保存到mongo");
            apUserSearch.setUserId(Integer.valueOf(BaseContext.getThreadId()
                    .toString()));
            ApUserSearch one = mongoTemplate.findOne(Query.query(Criteria.where("userId")
                    .is(BaseContext.getThreadId())
                    .and("keyword")
                    .is(searchWords)), ApUserSearch.class);
            if (one != null) {
                log.info("更新mongo中的时间");
                mongoTemplate.updateFirst(Query.query(Criteria.where("userId")
                        .is(BaseContext.getThreadId())
                        .and("keyword")
                        .is(searchWords)), new Update().set("createdTime", new Date()), ApUserSearch.class);
            } else {
                log.info("新增搜索关键词到mongo");
                mongoTemplate.insert(apUserSearch);
            }
        }
    }
}
