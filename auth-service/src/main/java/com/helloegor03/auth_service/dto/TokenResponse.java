package com.helloegor03.auth_service.dto;

public class TokenResponse {
    private String token;

    public String getToken() {
        return token;
    }

    public TokenResponse(String token) {
        this.token = token;
    }

}
