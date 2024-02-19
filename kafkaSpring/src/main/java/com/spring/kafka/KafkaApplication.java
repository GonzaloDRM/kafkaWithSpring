package com.spring.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

@Slf4j
@SpringBootApplication
public class KafkaApplication implements CommandLineRunner {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private KafkaListenerEndpointRegistry registry;

	@KafkaListener(id = "gonzaId", topics = "gonza-topic", autoStartup = "false", containerFactory = "listenerContainerFactory",
			groupId = "gonza-group", properties = {"max.poll.interval.ms:4000", "max.poll.records:10"})
	public void listen(List<ConsumerRecord<String, String>> messages){
		log.info("Start reading messages");
		for (ConsumerRecord<String, String> message : messages) {
			log.info("Partition = {}, Offset = {}, Key = {}, Value = {} ", message.partition(), message.offset(), message.key(), message.value());
		}
		log.info("Batch Complete");
	}

	public static void main(String[] args) {
		SpringApplication.run(KafkaApplication.class, args);
	}

	// Asincrono
	@Override
	public void run(String... args) throws Exception {
		for (int i = 0; i < 100; i++) {
			kafkaTemplate.send("gonza-topic", String.valueOf(i), String.format("Sample message %d", i));
		}
		log.info("Waiting to start");
		Thread.sleep(5000);
		log.info("Starting");
		registry.getListenerContainer("gonzaId").start();
		Thread.sleep(5000);
		registry.getListenerContainer("gonzaId").stop();
		log.info("End");

	}

	/**
	 * 	  Basic
	 *
	 * 	  @Override
	 *    public void run(String... args) throws Exception {
	 * 		kafkaTemplate.send("gonza-topic", "Sample message");
	 *    }
	 */

	/**
	 *      Sincrono
	 *
	 *      @Override
	 * 	    public void run(String... args) throws Exception {
	 * 		kafkaTemplate.send("gonza-topic", "Sample message ").get(100, TimeUnit.MILLISECONDS);
	 *    }
	 */

}
