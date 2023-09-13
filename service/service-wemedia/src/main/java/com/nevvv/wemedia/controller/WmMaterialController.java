package com.nevvv.wemedia.controller;


import com.nevvv.model.common.dtos.PageResponseResult;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.wemedia.dto.PageWemediaDto;
import com.nevvv.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 自媒体图文素材信息表 前端控制器
 * </p>
 *
 * @author author
 * @since 2023-06-03
 */
@RestController
@RequestMapping("/api/v1/material")
@Slf4j
public class WmMaterialController {
    @Autowired
    private WmMaterialService wmMaterialService;

    /**
     * 上传图片到素材库
     *
     * @param multipartFile
     * @return
     */
    @PostMapping("upload_picture")
    public ResponseResult upload(MultipartFile multipartFile) {
        log.info("前端上传图片，接收的参数为：{}", multipartFile);
        return wmMaterialService.load(multipartFile);
    }

    /**
     * 素材库分页查询
     *
     * @param wemediaDto
     * @return
     */
    @PostMapping("/list")
    public PageResponseResult list(@RequestBody PageWemediaDto wemediaDto) {
        log.info("前端分页查询素材,接收的参数为:{}", wemediaDto);
        return wmMaterialService.listService(wemediaDto);
    }

    /**
     * 素材收藏
     *
     * @param id
     * @return
     */
    @GetMapping("/collect/{id}")
    public ResponseResult collect(@PathVariable Integer id) {
        log.info("前端素材收藏请求,接收的参数为:{}", id);
        return wmMaterialService.collect(id);
    }

    /**
     * 素材删除
     * @param id
     * @return
     */
    @GetMapping("/del_picture/{id}")
    public ResponseResult delPic(@PathVariable Integer id) {
        log.info("前端删除素材，接收的参数为:{}", id);
        return wmMaterialService.delPic(id);
    }
}