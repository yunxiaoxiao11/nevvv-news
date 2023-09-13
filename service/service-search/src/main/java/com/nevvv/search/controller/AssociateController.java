package com.nevvv.search.controller;

import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.search.pojo.Associate;
import com.nevvv.search.service.AssociateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/associate")
@Slf4j
public class AssociateController {
    @Autowired
    private AssociateService associateService;
    @PostMapping("/search")
    public ResponseResult<List<Associate>> search(@RequestBody Map<String, String> map) {
        log.info("前端自动补全搜索,参数为:{}", map);
        return associateService.search(map);
    }
}
