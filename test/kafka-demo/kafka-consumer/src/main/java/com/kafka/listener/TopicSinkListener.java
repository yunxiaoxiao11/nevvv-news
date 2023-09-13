package com.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TopicSinkListener {
    @KafkaListener(topics = {"TopicSink"})
    public void receiveMessage(ConsumerRecord<String, String> record) {
        log.info(record.key() + " --> " + record.value());
    }
}