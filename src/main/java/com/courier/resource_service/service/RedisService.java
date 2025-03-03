package com.courier.resource_service.service;

public interface RedisService {
  void saveKeyValues(String publicKey, String authServiceSecret);

  String getPublicKey();

  String getAuthServiceSecret();

  boolean hasValidPublicKey();
}
