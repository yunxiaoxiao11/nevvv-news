package com.kafka.config;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@EnableBinding(KafkaStreamsProcessor.class)
public class KafkaStreamListener {
    @StreamListener("TopicSource")
    @SendTo("TopicSink")
    public KStream<String, String> process(KStream<String, String> input) {
        return input.groupByKey()
                .windowedBy(TimeWindows.of(Duration.ofSeconds(60)))
                .count()
                .toStream()
                .map(new KeyValueMapper<Windowed<String>, Long, KeyValue<String, String>>() {
                    @Override
                    public KeyValue<String, String> apply(Windowed<String> key, Long value) {
                        return new KeyValue<>(key.key(), String.valueOf(value));
                    }
                });
    }
}