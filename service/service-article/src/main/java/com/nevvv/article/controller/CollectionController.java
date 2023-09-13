package com.nevvv.article.controller;

import com.nevvv.article.service.CollectionService;
import com.nevvv.model.common.dtos.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class CollectionController {
    @Autowired
    private CollectionService collectionService;
    @PostMapping("/collection_behavior")
    public ResponseResult collection(@RequestBody Map<String, String> map) {
        log.info("前端用户收藏文章,接受的参数为:{}", map);
        return collectionService.collection(map);
    }
}
