package com.nevvv.user.controller;

import com.nevvv.model.common.user.dto.LoginDto;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.user.service.UserService;
import com.nevvv.utils.common.AppJwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/login")
@Api(tags = "用户相关接口")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation("用户登录")
    @PostMapping("/login_auth")
    public ResponseResult login(@RequestBody LoginDto loginDto) {
        log.info("用户登录，参数为：{}", loginDto);
        return userService.login(loginDto);
    }

    @GetMapping("/test")
    public ResponseResult test() {
        String token = AppJwtUtil.getToken(0L);
        return ResponseResult.okResult(token);
    }
}
