package com.example.shop.payments.service;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountService {
    void createAccount(UUID userId);
    void topUp(UUID userId, BigDecimal amount);
    BigDecimal getBalance(UUID userId);
    boolean tryDebit(UUID userId, BigDecimal amount);
}