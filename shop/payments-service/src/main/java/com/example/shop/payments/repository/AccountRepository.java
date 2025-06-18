package com.example.shop.payments.repository;

import com.example.shop.payments.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID>{}
