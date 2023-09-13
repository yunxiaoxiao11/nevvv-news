package com.nevvv.autotask.publish.rabbitMq;

import com.nevvv.model.common.wemedia.pojo.WmNews;
import feign.wmnews.WmNewsFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
//@Component
public class PublishMsgListener {
    @Autowired
    private WmNewsFeign newsFeign;

    @RabbitListener(queues = "dl.queue")
    public void mqListener(Message message) {
        log.info("监听到延迟发布新闻的id...{}", new String(message.getBody()));
        Integer newsId = Integer.valueOf(new String(message.getBody()));
        log.info("查找该id符合发布条件的文章");
        WmNews result = newsFeign.feignFindById(newsId)
                .getData();
        if (result != null) {
            log.info("调用远程开始发布!");
            newsFeign.publish(result);
        }
    }
}
