package com.github.maxamel.server.services;

import com.github.maxamel.server.web.dtos.ChallengeDto;

public interface KafkaAgentService {
	
	public void send(String topic, ChallengeDto chal);
	
	public void openTopic(String topic);
	
	public void closeTopic(String topic);

}
