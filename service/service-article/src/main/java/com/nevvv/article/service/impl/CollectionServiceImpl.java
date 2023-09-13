package com.nevvv.article.service.impl;

import com.nevvv.article.mapper.CollectionMapper;
import com.nevvv.article.service.CollectionService;
import com.nevvv.common.constant.BaseEnum;
import com.nevvv.model.common.article.pojo.Collection;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.utils.shareData.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class CollectionServiceImpl implements CollectionService {
    @Autowired
    private CollectionMapper collectionMapper;

    @Override
    public ResponseResult collection(Map<String, String> map) {
        Long articleId = Long.valueOf(map.get("entryId"));
        Integer operation = Integer.valueOf(map.get("operation"));
        if (operation == 0) {
            log.info("收藏文章到collection表...");
            Collection collection = new Collection();
            collection.setCollectionTime(new Date());
            collection.setArticleId(articleId);
            collection.setEntryId(Integer.valueOf(String.valueOf(BaseContext.getThreadId())));
            collection.setType(BaseEnum.IS_COLLECTION.getValue());
            collectionMapper.save(collection);
        } else {
            log.info("取消收藏文章从collection表...");
            collectionMapper.remove(articleId, BaseContext.getThreadId());
        }
        return ResponseResult.okResult();
    }
}
