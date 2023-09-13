package com.nevvv.search.controller;

import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.search.dto.PageSearchUserDto;
import com.nevvv.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/article/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @PostMapping("/search")
    public ResponseResult search(@RequestBody PageSearchUserDto pageSearchUserDto) {
        log.info("用户端发起搜索,参数为:{}", pageSearchUserDto);
        return searchService.search(pageSearchUserDto);
    }
}
