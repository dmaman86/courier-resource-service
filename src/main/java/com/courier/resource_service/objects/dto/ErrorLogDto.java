package com.courier.resource_service.objects.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorLogDto {

  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String message;
  private String exception;
  private String path;
}
