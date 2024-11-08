package br.com.microservice.stateful_atuth_api.domain.dto;

public record AuthRequest(String username, String password) {
}
