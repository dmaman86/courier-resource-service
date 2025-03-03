package com.courier.resource_service.objects.dto;

import java.time.LocalDateTime;

import com.courier.resource_service.objects.enums.ErrorSeverity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

  @Enumerated(EnumType.STRING)
  private ErrorSeverity severity;
}
