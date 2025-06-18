package com.example.shop.common.dto;

import java.math.BigDecimal;

public class BalanceResponse {
    private BigDecimal balance;

    public BalanceResponse() {}

    public BalanceResponse(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
