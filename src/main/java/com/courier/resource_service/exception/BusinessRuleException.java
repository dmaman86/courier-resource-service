package com.courier.resource_service.exception;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
      super(message);
    }
}
