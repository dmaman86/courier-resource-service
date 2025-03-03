package com.courier.resource_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.courier.resource_service.objects.dto.AuthInfoDto;

@Component
public class PublicKeyListener {

  @Autowired private RedisService redisService;

  @KafkaListener(topics = "public-key", groupId = "resource-service-group")
  public void listenPublicKey(AuthInfoDto authInfoDto) {
    redisService.saveKeyValues(authInfoDto.getPublicKey(), authInfoDto.getAuthServiceSecret());
  }
}
