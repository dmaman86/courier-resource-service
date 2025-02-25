package com.courier.resource_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.courier.resource_service.objects.dto.PublicKeyDto;

@Component
public class PublicKeyListener {

  @Autowired private JwtService jwtService;

  @KafkaListener(topics = "public-key", groupId = "resource-service-group")
  public void listenPublicKey(PublicKeyDto publicKeyDto) {
    jwtService.updatePublicKey(publicKeyDto);
  }
}
