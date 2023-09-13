package com.nevvv.search.service.impl;

import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.enums.HttpCodeEnum;
import com.nevvv.model.common.history.pojo.ApUserSearch;
import com.nevvv.search.service.HistoryService;
import com.nevvv.utils.shareData.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class HistoryServiceImpl implements HistoryService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseResult<List<ApUserSearch>> load() {
        Long threadId = BaseContext.getThreadId();
        if (threadId != null) {
            ApUserSearch apUserSearch = new ApUserSearch();
            apUserSearch.setUserId(Integer.valueOf(threadId.toString()));
            List<ApUserSearch> userSearchList = mongoTemplate.find(Query.query(Criteria.where("userId")
                            .is(threadId))
                    .with(Sort.by(Sort.Direction.DESC, "createdTime"))
                    .with(PageRequest.of(0, 10)), ApUserSearch.class);
            return ResponseResult.okResult(userSearchList);
        }
        return ResponseResult.okResult(Collections.EMPTY_LIST);
    }

    @Override
    public ResponseResult del(String id) {
        ApUserSearch one = mongoTemplate.findOne(Query.query(Criteria.where("_id")
                .is(id)), ApUserSearch.class);
        if (one != null) {
            log.info("存在这个关键词");
            mongoTemplate.remove(Query.query(Criteria.where("id")
                    .is(id)), ApUserSearch.class);
            return ResponseResult.okResult();
        } else {
            log.info("不存在这个关键词");
            return ResponseResult.errorResult(HttpCodeEnum.DATA_NOT_EXIST);
        }
    }
}