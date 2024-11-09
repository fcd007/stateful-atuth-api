package br.com.microservice.stateful_atuth_api.core.controller;

import br.com.microservice.stateful_atuth_api.core.service.AuthService;
import br.com.microservice.stateful_atuth_api.domain.dto.*;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Log4j2
@RequestMapping("api/v1/auth")
public class AuthControler {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<TokenDTO> login(@RequestBody AuthRequest request) {
    log.info("Request received to login");

    var token = authService.login(request);

    log.info("Logged in successfully");
    return ResponseEntity.ok(token);
  }

  @PostMapping("/token/validate")
  public ResponseEntity<TokenDTO> validateToken(@RequestHeader String accessToken) {
    log.info("Request received to validate token");

    var token = authService.validateToken(accessToken);

    log.info("Token validated successfully");
    return ResponseEntity.ok(token);
  }

  @PostMapping("/logout")
  public Map<String, Object> logout(@RequestHeader String accessToken) {
    log.info("Request received to logout");

    authService.logout(accessToken);
    var response = new HashMap<String, Object>();
    var ok = HttpStatus.OK;
    response.put("status", ok.name());
    response.put("code", ok.value());

    log.info("Logged out successfully");

    return response;
  }

  @GetMapping("/user")
  public AuthUserResponse getAuthenticatedUser(@RequestHeader String accessToken) {
    log.info("Request received to get user");

    var authenticatedUser = authService.getAuthenticatedUser(accessToken);

    log.info("User authenticated successfully");

    return authenticatedUser;
  }
}
