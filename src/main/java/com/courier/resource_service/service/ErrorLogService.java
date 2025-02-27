package com.courier.resource_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.courier.resource_service.objects.dto.ErrorLogDto;

@Service
public class ErrorLogService {

  @Autowired private KafkaTemplate<String, Object> kafkaTemplate;

  public void sendErrorLog(ErrorLogDto errorLogDto) {
    kafkaTemplate.send("error-topic", errorLogDto);
  }
}
