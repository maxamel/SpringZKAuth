package com.github.maxamel.server.services;

import static org.junit.Assert.assertThat;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import com.github.maxamel.server.services.impl.KafkaAgentServiceImpl;
import com.github.maxamel.server.services.mapping.MappingBasePackage;
import com.github.maxamel.server.web.dtos.ChallengeDto;
import com.github.rozidan.springboot.modelmapper.WithModelMapper;

import static org.springframework.kafka.test.assertj.KafkaConditions.key;
import static org.springframework.kafka.test.hamcrest.KafkaMatchers.hasValue;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
@WithModelMapper(basePackageClasses = MappingBasePackage.class)
@SpringBootTest
public class KafkaAgentServiceTest {

	@Autowired
	private KafkaAgentService sender;
	
	@Value("${test.passwordless}")
	private String pass;

	private final static String topic = "topic";

	private KafkaMessageListenerContainer<String, ChallengeDto> container;

	private BlockingQueue<ConsumerRecord<String, ChallengeDto>> records;

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, topic);

	@Before
	public void setUp() throws Exception {
		// set up the Kafka consumer properties
		Map<String, Object> consumerProperties =
				KafkaTestUtils.consumerProps("sender", "false", embeddedKafka);

		// create a Kafka consumer factory
		DefaultKafkaConsumerFactory<String, ChallengeDto> consumerFactory =
				new DefaultKafkaConsumerFactory<String, ChallengeDto>(consumerProperties);

		// set the topic that needs to be consumed
		ContainerProperties containerProperties = new ContainerProperties(topic);

		// create a Kafka MessageListenerContainer
		container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

		// create a thread safe queue to store the received message
		records = new LinkedBlockingQueue<>();

		// setup a Kafka message listener
		container.setupMessageListener(new MessageListener<String, ChallengeDto>() {
			@Override
			public void onMessage(ConsumerRecord<String, ChallengeDto> record) {
				records.add(record);
			}
		});

		// start the container and underlying message listener
		container.start();

		// wait until the container has the required number of assigned partitions
		ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());

		sender = new KafkaAgentServiceImpl(embeddedKafka.getZookeeperConnectionString());
	}

	@After
	public void tearDown() {
		// stop the container
		container.stop();
	}

	@Test
	public void testSend() throws InterruptedException {
		// send the message
		//ChallengeDto dto = ChallengeDto.builder().challenge(pass).build();
		//sender.send("topic", dto);

		// check that the message was received
		//ConsumerRecord<String, ChallengeDto> received = records.poll(10, TimeUnit.SECONDS);
		// Hamcrest Matchers to check the value
		//assertThat(received, hasValue(dto));
		// AssertJ Condition to check the key
		//assertThat(received).has(key(null));
		
	}

}
