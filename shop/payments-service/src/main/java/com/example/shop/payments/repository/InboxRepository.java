package com.example.shop.payments.repository;

import com.example.shop.payments.entity.InboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface InboxRepository extends JpaRepository<InboxMessage, UUID>{
    List<InboxMessage> findByStatus(String status);
}
