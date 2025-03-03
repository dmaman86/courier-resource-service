package com.courier.resource_service.service;

public interface BlackListService {

  void handleUserDisabledEvent(Long userId);

  void cleanExpiredBlackListUsers();

  boolean isUserBlackListed(Long userId);
}
