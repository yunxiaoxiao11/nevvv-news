package com.nevvv.autotask.publish.task;

import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.enums.HttpCodeEnum;
import com.nevvv.model.common.wemedia.pojo.WmNews;
import feign.wmnews.WmNewsFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class RedisZsetAutoPublish {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private WmNewsFeign newsFeign;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void publish() {
        log.info("查找redis中保存的所有发布时间小于当前时间的news id");
        Set<String> newsIdList = redisTemplate.opsForZSet()
                .rangeByScore("wmNews", 0, System.currentTimeMillis());
        if (newsIdList != null && !newsIdList.isEmpty()) {
            for (String id : newsIdList) {
                ResponseResult<WmNews> result = newsFeign.feignFindById(Integer.valueOf(id));
                if (result.getData() != null && result.getCode() == HttpCodeEnum.SUCCESS.getCode()) {
                    WmNews data = result.getData();
                    newsFeign.publish(data);
                }
                log.info("移除redis中的指定数据");
                redisTemplate.opsForZSet()
                        .remove("wmNews", id);
            }
        }
    }
}
