package com.spring.kafka;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Slf4j
@SpringBootApplication
public class KafkaApplication {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private MeterRegistry meterRegistry;

	@KafkaListener(id = "gonzaId", topics = "gonza-topic", autoStartup = "true", containerFactory = "listenerContainerFactory",
			groupId = "gonza-group", properties = {"max.poll.interval.ms:4000", "max.poll.records:50"})
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

	@Scheduled(fixedDelay = 2000, initialDelay = 100) // ejecuta el metodo cada cierto tiempo
	public void sendKafkaMessages(){
		for (int i = 0; i < 200; i++) {
			kafkaTemplate.send("gonza-topic", String.valueOf(i), String.format("Sample message %d", i));
		}
	}

	// Metodo para medir las metricas mas facil
	@Scheduled(fixedDelay = 2000, initialDelay = 500)
	public void printMetrics(){
		List<Meter> metrics = meterRegistry.getMeters();
		for (Meter meter : metrics) {
			log.info("Meter = {} ", meter.getId().getName()); // imprimo todas las metricas que existen
		}

		double count = meterRegistry.get("kafka.producer.record.send.total").functionCounter().count();
		log.info("Count {} ", count);
	}

}
