package br.com.inovationtech.FoodMenuMasterApi.DTO;

public record LoginResponseDTO (
    String token,
    String tokenType,
    long expiresInMs
) {}
