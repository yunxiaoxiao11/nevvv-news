package com.nevvv.article.listener;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.nevvv.article.service.ArticleConfigService;
import com.nevvv.model.common.article.pojo.ArticleConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ArticleUpOrDown {
    @Autowired
    private ArticleConfigService configService;

    @KafkaListener(topics = "news_down_up_topic")
    public void articleUpOrDown(ConsumerRecord<String, String> record) {
        log.info("kafka消费者监听到上下架消息,参数为:{}",record);
        String key = record.key();
        String value = record.value();
        LambdaUpdateWrapper<ArticleConfig> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ArticleConfig::getArticleId, Long.valueOf(key));
        updateWrapper.set(ArticleConfig::getIsDown, !Boolean.valueOf(value));
        log.info("开始更新文章上下架状态...");
        configService.update(updateWrapper);
    }
}