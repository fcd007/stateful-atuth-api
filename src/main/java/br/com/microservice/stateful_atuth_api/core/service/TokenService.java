package br.com.microservice.stateful_atuth_api.core.service;

import static org.springframework.util.ObjectUtils.isEmpty;

import br.com.microservice.stateful_atuth_api.domain.dto.TokenDTO;
import br.com.microservice.stateful_atuth_api.domain.dto.TokenData;
import br.com.microservice.stateful_atuth_api.infra.exception.AuthenticationException;
import br.com.microservice.stateful_atuth_api.infra.exception.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TokenService {

  private static final Long DAY_IN_SECONDS_TOKEN = 864000L;
  private static final Integer TOKEN_INDEX = 1;
  private static final String EMPTY_STRING_SPACE = " ";

  private RedisTemplate<String, String> redisTemplate;
  private ObjectMapper objectMapper;

  public String createToken(String username) {
    var accessToken = UUID.randomUUID().toString();
    var data = new TokenData(username);
    var jsonData = getJsonData(data);

    redisTemplate.opsForValue().set(accessToken, jsonData);
    redisTemplate.expireAt(accessToken, Instant.now().plusSeconds(DAY_IN_SECONDS_TOKEN));
    return accessToken;
  }

  private String getJsonData(Object payload) {
    try {
      return objectMapper.writeValueAsString(payload);
    } catch (JsonProcessingException exception) {
      throw new RuntimeException(exception.getMessage());
    }
  }

  public TokenData getTokenData(String token) {
    var accessToken = extractToken(token);
    var jsonString = getRedisTokenValue(accessToken);

    try {
      return objectMapper.readValue(jsonString, TokenData.class);
    } catch (Exception e) {
      throw new AuthenticationException("Error extract the authenticated user");
    }
  }

  public boolean validateAccessToken(String token) {
    var accessToken = extractToken(token);
    var data = getRedisTokenValue(accessToken);
    return !isEmpty(data);

  }

  private String getRedisTokenValue(String token) {
    return redisTemplate.opsForValue().get(token);
  }

  public void deleteRedisToken(String token) {
    var accessToken = extractToken(token);
    redisTemplate.delete(accessToken);
  }

  private String extractToken(String token) {
    if (isEmpty(token)) {
      throw new ValidationException("The access Token is required");
    }

    if (!token.contains(EMPTY_STRING_SPACE)) {
      String[] tokenParts = token.split(EMPTY_STRING_SPACE);

      if (tokenParts.length > 1) {
        return tokenParts[TOKEN_INDEX];
      }
    }

    return token;
  }
}
