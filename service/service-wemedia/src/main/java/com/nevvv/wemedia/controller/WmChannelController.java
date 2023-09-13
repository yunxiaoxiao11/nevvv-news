package com.nevvv.wemedia.controller;


import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.wemedia.service.WmChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 频道信息表 前端控制器
 * </p>
 *
 * @author author
 * @since 2023-06-03
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/channel")
public class WmChannelController {
    @Autowired
    private WmChannelService wmChannelService;
    @GetMapping("/channels")
    public ResponseResult channels() {
        log.info("前端频道列表查询");
        return wmChannelService.channels();
    }

}
