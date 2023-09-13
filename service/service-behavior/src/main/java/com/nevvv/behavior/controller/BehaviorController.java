package com.nevvv.behavior.controller;

import com.nevvv.behavior.service.BehaviorService;
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
public class BehaviorController {
    @Autowired
    private BehaviorService behaviorService;

    @PostMapping("/likes_behavior")
    public ResponseResult like(@RequestBody Map<String,String> map) {
        log.info("前端对文章点赞,接收的参数为:{}", map);
        return behaviorService.like(map);
    }

    @PostMapping("/read_behavior")
    public ResponseResult read(@RequestBody Map<String,Long> map) {
        log.info("前端点击文章阅读,接受的参数为:{}", map);
        return behaviorService.read(map);
    }
}
