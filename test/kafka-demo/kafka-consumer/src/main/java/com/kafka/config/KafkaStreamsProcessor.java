package com.kafka.config;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

/**
 * 定义Topic主题和Sink主题
 */
public interface KafkaStreamsProcessor {
	@Input("TopicSource")
	KStream<String, String> input();

	@Output("TopicSink")
	KStream<String, String> output();
}