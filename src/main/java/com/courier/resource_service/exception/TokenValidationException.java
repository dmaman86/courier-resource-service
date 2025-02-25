package com.courier.resource_service.exception;

public class TokenValidationException extends RuntimeException {
    public TokenValidationException(String message) {
      super(message);
    }
}
