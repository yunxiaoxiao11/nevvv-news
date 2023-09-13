package com.nevvv.search.controller;

import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.history.pojo.ApUserSearch;
import com.nevvv.search.service.HistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/history")
@Slf4j
public class HistoryController {
    @Autowired
    private HistoryService historyService;
    @PostMapping("/load")
    public ResponseResult<List<ApUserSearch>> load() {
        log.info("前端按照当前用户查找搜索历史");
        return historyService.load();
    }

    @PostMapping("/del")
    public ResponseResult del(@RequestBody Map<String,String> map) {
        log.info("前端删除指定用户的搜索历史,参数为:{}", map.get("id"));
        return historyService.del(map.get("id"));
    }
}
