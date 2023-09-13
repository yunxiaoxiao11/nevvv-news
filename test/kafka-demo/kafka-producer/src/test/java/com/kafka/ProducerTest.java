package com.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
public class ProducerTest {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Test
    public void test01() {
        kafkaTemplate.send("TopicSource", "hello", "itcast");
        kafkaTemplate.send("TopicSource", "hello", "kitty");
        kafkaTemplate.send("TopicSource", "hello", "world");
        kafkaTemplate.send("TopicSource", "say", "good");
        kafkaTemplate.send("TopicSource", "say", "byebye");
        kafkaTemplate.send("TopicSource", "love", "mm");
    }
}