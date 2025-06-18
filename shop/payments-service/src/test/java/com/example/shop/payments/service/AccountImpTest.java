package com.example.shop.payments.service;

import com.example.shop.payments.entity.Account;
import com.example.shop.payments.entity.TransactionLog;
import com.example.shop.payments.repository.AccountRepository;
import com.example.shop.payments.repository.TransactionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountImpTest {
    private AccountRepository accountRepository;
    private TransactionLogRepository txRepository;
    private AccountServiceImp accountService;
    private UUID userId;

    @BeforeEach
    public void setUp() {
        accountRepository = mock(AccountRepository.class);
        txRepository = mock(TransactionLogRepository.class);
        accountService = new AccountServiceImp(accountRepository, txRepository);
        userId = UUID.randomUUID();
    }

    @Test
    void createAccount_savesNewAccount() {
        when(accountRepository.existsById(userId)).thenReturn(false);

        accountService.createAccount(userId);
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());
        assertThat(accountCaptor.getValue().getUserId()).isEqualTo(userId);
        assertThat(accountCaptor.getValue().getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void createAccount_existing_throwsException() {
        when(accountRepository.existsById(userId)).thenReturn(true);
        assertThatThrownBy(() -> accountService.createAccount(userId)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Аккаунт уже существует для пользователя с id: " + userId);
    }

    @Test
    void tryDebit_insufficientFunds_returnsFalseAndLogsFailedTx() {
        Account acct = new Account(userId);
        acct.setBalance(BigDecimal.valueOf(10));
        when(accountRepository.findById(userId)).thenReturn(Optional.of(acct));

        boolean result = accountService.tryDebit(userId, BigDecimal.valueOf(50));

        assertThat(result).isFalse();
        ArgumentCaptor<TransactionLog> txCaptor = ArgumentCaptor.forClass(TransactionLog.class);
        verify(txRepository, times(2)).save(txCaptor.capture());
        TransactionLog failed = txCaptor.getAllValues().get(1);
        assertThat(failed.getStatus()).isEqualTo("FAILED");
    }

    @Test
    void tryDebit_sufficientFunds_returnsTrueAndUpdatesBalanceAndLogsCompleted() {
        Account acct = new Account(userId);
        acct.setBalance(BigDecimal.valueOf(100));
        when(accountRepository.findById(userId)).thenReturn(Optional.of(acct));

        boolean result = accountService.tryDebit(userId, BigDecimal.valueOf(40));

        assertThat(result).isTrue();
        assertThat(acct.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(60));
        ArgumentCaptor<TransactionLog> txCaptor = ArgumentCaptor.forClass(TransactionLog.class);
        verify(txRepository, times(2)).save(txCaptor.capture());
        TransactionLog completed = txCaptor.getAllValues().get(1);
        assertThat(completed.getStatus()).isEqualTo("COMPLETED");
    }
}
