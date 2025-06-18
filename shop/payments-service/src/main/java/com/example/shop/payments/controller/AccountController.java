package com.example.shop.payments.controller;

import com.example.shop.common.dto.BalanceResponse;
import com.example.shop.common.dto.CreateAccountRequest;
import com.example.shop.common.dto.TopUpRequest;
import com.example.shop.payments.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;

@Tag(name = "Accounts", description = "Управление счетами пользователей")
@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Создание счета пользователя", description = "Создает новый счет для пользователя по его идентификатору")
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreateAccountRequest request) {
        accountService.createAccount(request.getUserId());
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "Пополнение счета пользователя", description = "Позволяет пополнить счет пользователя на указанную сумму")
    @PostMapping("/{userId}/topup")
    public ResponseEntity<Void> topUp(@PathVariable UUID userId, @RequestBody TopUpRequest request) {
        accountService.topUp(userId, request.getAmount());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Проверка баланса пользователя", description = "Возвращает текущий баланс счета пользователя по его идентификатору")
    @GetMapping("/{userId}/balance")
    public ResponseEntity<BalanceResponse> balance(@PathVariable UUID userId) {
        return ResponseEntity.ok(new BalanceResponse(accountService.getBalance(userId)));
    }
}
