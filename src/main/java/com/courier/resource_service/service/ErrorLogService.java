package com.courier.resource_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.courier.resource_service.objects.dto.ErrorLogDto;

@Service
public class ErrorLogService {

  @Autowired private KafkaTemplate<String, ErrorLogDto> errorLogDtoKafkaTemplate;

  public void sendErrorLog(ErrorLogDto errorLogDto) {
    errorLogDtoKafkaTemplate.send("error-topic", errorLogDto);
  }
}
