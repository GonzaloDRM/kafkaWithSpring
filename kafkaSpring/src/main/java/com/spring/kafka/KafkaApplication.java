package com.spring.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
public class KafkaApplication implements CommandLineRunner {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@KafkaListener(topics = "gonza-topic", groupId = "gonza-group")
	public void listen(String message){
		log.info("Message received {}", message);
	}

	public static void main(String[] args) {
		SpringApplication.run(KafkaApplication.class, args);
	}

	// Asincrono
	@Override
	public void run(String... args) throws Exception {
		CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("gonza-topic", "Sample message");

		future.whenComplete((result, exception) -> {
			if (exception == null) {
				log.info("Message sent, offset: {}", result.getRecordMetadata().offset());
			} else {
				log.error("Error sending message:", exception);
			}
		});
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
