package com.example.shop.common.dto;

import java.util.UUID;

public class CreateAccountRequest {
    private UUID userId;

    public CreateAccountRequest() {}

    public CreateAccountRequest(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }    
}
