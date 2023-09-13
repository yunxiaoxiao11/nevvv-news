package com.nevvv.user.controller;

import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.user.service.UserFanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserFanController {
    @Autowired
    private UserFanService fanService;
    @PostMapping("/user_follow")
    public ResponseResult follow(@RequestBody Map<String, String> map) {
        return fanService.follow(map);
    }

    @PostMapping("/getReviewInfo")
    public ResponseResult getReviewInfo(@RequestBody Map<String, String> map) {
        log.info("远程调用用户服务查找该用户的关注者,接收的参数为:{}", map);
        return fanService.getReviewInfo(map);
    }
}
