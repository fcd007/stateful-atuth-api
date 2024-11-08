package br.com.microservice.stateful_atuth_api.core.service;

import static org.springframework.util.ObjectUtils.isEmpty;

import br.com.microservice.stateful_atuth_api.core.repository.UserRepository;
import br.com.microservice.stateful_atuth_api.domain.dto.AuthRequest;
import br.com.microservice.stateful_atuth_api.domain.dto.AuthUserResponse;
import br.com.microservice.stateful_atuth_api.domain.dto.TokenDTO;
import br.com.microservice.stateful_atuth_api.domain.model.User;
import br.com.microservice.stateful_atuth_api.infra.exception.AuthenticationException;
import br.com.microservice.stateful_atuth_api.infra.exception.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;

  public TokenDTO login(AuthRequest authRequest) {
    var user = findByUsername(authRequest.username());

    var accessToken = tokenService.createToken(user.getUsername());
    validatePassword(authRequest.password(), user.getPassword());
    return new TokenDTO(accessToken);
  }

  private void validatePassword(String rawPassword, String encodedPassword) {
    if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
      throw new ValidationException("Invalid password");
    }
  }

  public AuthUserResponse getAuthenticatedUser(String accessToken) {
    var tokenData = tokenService.getTokenData(accessToken);
    var user = findByUsername(tokenData.username());
    return new AuthUserResponse(user.getId(), user.getUsername());
  }

  public void logout(String accessToken) {
    tokenService.deleteRedisToken(accessToken);
  }

  public TokenDTO validateToken(String accessToken) {
    validateExistingToken(accessToken);
    var valid = tokenService.validateAccessToken(accessToken);
    if (valid) {
      return new TokenDTO(accessToken);
    }

    throw new AuthenticationException("Invalid access token");
  }

  private void validateExistingToken(String accessToken) {
    if (isEmpty(accessToken)) {
      throw new ValidationException("The access token not must be informed");
    }
  }

  private User findByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new ValidationException("User not found"));
  }
}
