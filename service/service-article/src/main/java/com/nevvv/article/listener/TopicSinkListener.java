package com.nevvv.article.listener;

import com.nevvv.article.service.ArticleService;
import com.nevvv.model.common.article.pojo.Article;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TopicSinkListener {
    @Autowired
    private ArticleService articleService;

    @KafkaListener(topics = {"TopicSink"})
    public void receiveMessage(ConsumerRecord<String, String> record) {
        log.info(record.key() + " --> " + record.value());
        String key = record.key();
        Integer value = Integer.valueOf(record.value());
        String[] split = key.split(":");
        Long id = Long.valueOf(split[1]);
        Article byId = articleService.getById(id);
        if ("read".equals(split[0])) {
            log.info("用户阅读行为修改art...");
            byId.setViews(byId.getViews() + value);
        } else {
            log.info("用户点赞行为修改art...");
            if ("0".equals(split[2])) {
                log.info("用户点赞...");
                byId.setLikes(byId.getLikes() + value);
            } else {
                log.info("用户取消点赞...");
                byId.setLikes(byId.getLikes() - value);
            }
        }
        articleService.updateById(byId);
    }
}