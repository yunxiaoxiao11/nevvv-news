package com.nevvv.wemedia.controller;


import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.wemedia.dto.PageWeNewsDto;
import com.nevvv.model.common.wemedia.dto.WeDownUpDto;
import com.nevvv.model.common.wemedia.dto.WeNewsDto;
import com.nevvv.model.common.wemedia.pojo.WmNews;
import com.nevvv.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 自媒体图文内容信息表 前端控制器
 * </p>
 *
 * @author author
 * @since 2023-06-03
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/news")
public class WmNewsController {
    @Autowired
    private WmNewsService wmNewsService;

    /**
     * 待条件的分页查询自媒体文章列表
     *
     * @param pageWeNewsDto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult list(@RequestBody PageWeNewsDto pageWeNewsDto) {
        log.info("前端分页查询自媒体文章列表:{}", pageWeNewsDto);
        return wmNewsService.listService(pageWeNewsDto);
    }

    /**
     * 发布或修改文章
     *
     * @param weNewsDto
     * @return
     */
    @PostMapping("/submit")
    public ResponseResult submit(@RequestBody WeNewsDto weNewsDto) throws Exception {
        log.info("前端发布文章,接收的参数为:{}", weNewsDto);
        return wmNewsService.submit(weNewsDto);
    }

    /**
     * 修改文章回显
     *
     * @param id
     * @return
     */
    @GetMapping("/one/{id}")
    public ResponseResult<WmNews> update(@PathVariable Integer id) {
        log.info("前端修改文章信息,接收的参数为:{}", id);
        return ResponseResult.okResult(wmNewsService.getById(id));
    }

    /**
     * 根据id查找符合延迟发布的文章
     * @param id
     * @return
     */
    @GetMapping("/feign_find/{id}")
    public ResponseResult<WmNews> feignFindById(@PathVariable Integer id) {
        log.info("前端修改文章信息,接收的参数为:{}", id);
        return ResponseResult.okResult(wmNewsService.findById(id));
    }

    /**
     * 删除文章信息
     *
     * @param id
     * @return
     */
    @GetMapping("del_news/{id}")
    public ResponseResult delNews(@PathVariable Integer id) {
        log.info("前端删除文章信息,接收的参数为:{}", id);
        return wmNewsService.delById(id);
    }

    /**
     * 远程调用查找审核通过未发布的文章
     * @return
     */
    @GetMapping("/getEnablePubArticle")
    public ResponseResult<List<WmNews>> getEnablePubArticle() {
        log.info("远程调用查找审核通过未发布的文章");
        return wmNewsService.getArticle();
    }

    /**
     * 远程调用发布文章
     * @param news
     * @return
     */
    @PostMapping("/publish")
    public ResponseResult publish(@RequestBody WmNews news) throws Exception {
        log.info("远程调用发布文章,接收的参数为:{}",news);
        return wmNewsService.publish(news);
    }

    /**
     * 上下架文章kafka
     * @param downUpDto
     * @return
     */
    @PostMapping("/down_or_up")
    public ResponseResult downOrUp(@RequestBody WeDownUpDto downUpDto) {
        log.info("前端上下架文章,参数为:{}", downUpDto);
        return wmNewsService.downOrUp(downUpDto);
    }
}
