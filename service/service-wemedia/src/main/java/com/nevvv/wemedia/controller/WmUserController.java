package com.nevvv.wemedia.controller;

import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.wemedia.dto.WemediaDto;
import com.nevvv.wemedia.service.WmUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 自媒体用户信息表 前端控制器
 * </p>
 *
 * @author
 * @since 2023-06-02
 */
@Slf4j
@RestController
@RequestMapping("/login")
public class WmUserController {
    @Autowired
    private WmUserService wmUserService;

    @PostMapping("/in")
    public ResponseResult login(@RequestBody WemediaDto wemediaDto) {
        log.info("前端自媒体用户登录,参数为:{}", wemediaDto);
        return wmUserService.login(wemediaDto);
    }
}