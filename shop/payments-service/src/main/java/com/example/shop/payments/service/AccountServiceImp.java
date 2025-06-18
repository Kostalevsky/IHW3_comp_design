package com.example.shop.payments.service;

import com.example.shop.payments.entity.Account;
import com.example.shop.payments.entity.TransactionLog;
import com.example.shop.payments.repository.AccountRepository;
import com.example.shop.payments.repository.TransactionLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class AccountServiceImp implements AccountService {
    private final AccountRepository accountRepository;
    private final TransactionLogRepository txRepository;

    public AccountServiceImp(AccountRepository accountRepository, TransactionLogRepository txRepository) {
        this.accountRepository = accountRepository;
        this.txRepository = txRepository;
    }

    @Override
    @Transactional
    public void createAccount(UUID userId) {
        if (accountRepository.existsById(userId)) {
            throw new IllegalArgumentException("Аккаунт уже существует для пользователя с id: " + userId);
        }
        accountRepository.save(new Account(userId));
    }

    @Override
    @Transactional
    public void topUp(UUID userId, BigDecimal amount) {
        Account acct = accountRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Аккаунт не найден для пользователя с id: " + userId));
        acct.setBalance(acct.getBalance().add(amount));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalance(UUID userId) {
        return accountRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Аккаунт не найден для пользователя с id: " + userId)).getBalance();
    }

    @Override
    @Transactional
    public boolean tryDebit(UUID userId, BigDecimal amount) {
        TransactionLog tx = new TransactionLog(
            UUID.randomUUID(),
            userId,
            amount,
            "PENDING",
            Instant.now()
        );
        txRepository.save(tx);

        Account acct = accountRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if (acct.getBalance().compareTo(amount) < 0) {
            tx.setStatus("FAILED");
            tx.setProcessedAt(Instant.now());
            txRepository.save(tx);
            return false;
        }

        acct.setBalance(acct.getBalance().subtract(amount));
       
        tx.setStatus("COMPLETED");
        tx.setProcessedAt(Instant.now());
        txRepository.save(tx);

        return true;
    }
}
