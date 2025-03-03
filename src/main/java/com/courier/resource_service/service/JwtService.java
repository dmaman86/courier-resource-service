package com.courier.resource_service.service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.courier.resource_service.exception.PublicKeyException;
import com.courier.resource_service.objects.dto.UserContext;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {

  @Autowired private RedisService redisService;

  public boolean isTokenValid(String token) {
    return extractExpiration(token).after(new Date());
  }

  public UserContext getUserContext(String token) {
    Claims claims = parseTokenClaims(token);
    Set<String> roles = Set.of(claims.get("roles").toString().split(","));

    return UserContext.builder()
        .id(Long.parseLong(claims.get("id").toString()))
        .fullName(claims.get("fullName", String.class))
        .phoneNumber(claims.get("phoneNumber", String.class))
        .email(claims.get("email", String.class))
        .roles(roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()))
        .build();
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = parseTokenClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims parseTokenClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getPublicKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private PublicKey getPublicKey() {
    String publicKeyStr = redisService.getPublicKey();
    if (publicKeyStr == null) {
      throw new PublicKeyException("Public key has not been set yet.");
    }

    try {
      byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return keyFactory.generatePublic(keySpec);

    } catch (Exception e) {
      throw new RuntimeException("Error loading public key from Redis", e);
    }
  }
}
