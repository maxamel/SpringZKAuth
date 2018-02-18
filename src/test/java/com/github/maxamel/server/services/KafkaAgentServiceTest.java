package com.github.maxamel.server.services;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import com.github.maxamel.server.services.impl.KafkaAgentServiceImpl;
import com.github.maxamel.server.services.mapping.MappingBasePackage;
import com.github.maxamel.server.web.dtos.ChallengeDto;
import com.github.rozidan.springboot.modelmapper.WithModelMapper;


@RunWith(MockitoJUnitRunner.class)
@WithModelMapper(basePackageClasses = MappingBasePackage.class)
@SpringBootTest
public class KafkaAgentServiceTest {

	@Autowired
	private KafkaAgentService sender;
	
	private final static String topic = "topic";

	private KafkaMessageListenerContainer<String, String> container;

	private BlockingQueue<ConsumerRecord<String, String>> records;

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, topic);

	@Before
	public void setUp() throws Exception {
		// set up the Kafka consumer properties
		Map<String, Object> consumerProperties =
				KafkaTestUtils.consumerProps("sender", "false", embeddedKafka);

		// create a Kafka consumer factory
		DefaultKafkaConsumerFactory<String, String> consumerFactory =
				new DefaultKafkaConsumerFactory<String, String>(consumerProperties);

		// set the topic that needs to be consumed
		ContainerProperties containerProperties = new ContainerProperties(topic);

		// create a Kafka MessageListenerContainer
		container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

		// create a thread safe queue to store the received message
		records = new LinkedBlockingQueue<>();

		// setup a Kafka message listener
		container.setupMessageListener(new MessageListener<String, String>() {
			@Override
			public void onMessage(ConsumerRecord<String, String> record) {
				records.add(record);
			}
		});

		// start the container and underlying message listener
		container.start();

		Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        ProducerFactory<String, ChallengeDto> pf = new DefaultKafkaProducerFactory<String, ChallengeDto>(props);
        KafkaTemplate<String, ChallengeDto> template = new KafkaTemplate<>(pf);
		// wait until the container has the required number of assigned partitions
		ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());

		sender = new KafkaAgentServiceImpl(embeddedKafka.getZookeeperConnectionString(), template);
	}

	@After
	public void tearDown() {
		// stop the container
		container.stop();
	}

	@Test
	public void testSend() throws InterruptedException {
	    
		// send the message
		ChallengeDto dto = ChallengeDto.builder().challenge("challenge").build();
		sender.send(topic, dto);

		// check that the message was received
		ConsumerRecord<String, String> received = records.poll(10, TimeUnit.SECONDS);
		// Hamcrest Matchers to check the value
		Assert.assertTrue(received.topic().equals(topic));
		Assert.assertTrue(received.key() == null);
		Assert.assertTrue(received.value().contains("challenge"));
	}
}
