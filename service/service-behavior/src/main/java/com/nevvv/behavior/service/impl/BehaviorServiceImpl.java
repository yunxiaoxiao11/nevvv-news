package com.nevvv.behavior.service.impl;

import com.nevvv.behavior.service.BehaviorService;
import com.nevvv.common.constant.BaseEnum;
import com.nevvv.model.common.dtos.ResponseResult;
import com.nevvv.model.common.enums.HttpCodeEnum;
import com.nevvv.utils.shareData.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BehaviorServiceImpl implements BehaviorService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Override
    public ResponseResult read(Map<String, Long> map) {
        Long threadId = BaseContext.getThreadId();
        if (threadId == null) {
            ResponseResult.errorResult(HttpCodeEnum.NO_OPERATOR_AUTH);
        }
        redisTemplate.opsForHash()
                .increment(BaseEnum.READ_PRE.getName() + map.get("articleId"), threadId.toString(), 1);
        kafkaTemplate.send(BaseEnum.KAFKA_SOURCE.getName(), BaseEnum.READ_PRE.getName() + map.get("articleId"), "1");
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult like(Map<String, String> map) {
        Long threadId = BaseContext.getThreadId();
        if (threadId == null) {
            ResponseResult.errorResult(HttpCodeEnum.NO_OPERATOR_AUTH);
        }
        redisTemplate.opsForHash()
                .put(BaseEnum.LIKE_PRE.getName() + map.get("articleId"), threadId.toString(), map.get("operation"));
        kafkaTemplate.send(BaseEnum.KAFKA_SOURCE.getName(), BaseEnum.LIKE_PRE.getName() + map.get("articleId") + ":" + map.get("operation"), "1");
        return ResponseResult.okResult();
    }
}