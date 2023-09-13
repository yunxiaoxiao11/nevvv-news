package com.nevvv.article.task;

import com.nevvv.article.service.ArticleService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SampleXxlJob {
    @Autowired
    private ArticleService articleService;

    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("set-hot-article")
    public void demoJobHandler() throws Exception {
        log.info("处理热点新闻...");
        articleService.hotNews();
    }
}