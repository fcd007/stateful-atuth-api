package br.com.microservice.stateful_atuth_api.infra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalHandlerException {

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<?> handlerValidationException(ValidationException validationException) {
    var details = new ExceptionDetails(HttpStatus.BAD_REQUEST.value(), validationException.getMessage());
    return ResponseEntity.badRequest().body(details);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<?> handlerValidationException(AuthenticationException authenticationException) {
    var details = new ExceptionDetails(HttpStatus.UNAUTHORIZED.value(), authenticationException.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(details);
  }
}
