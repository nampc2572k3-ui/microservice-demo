package com.example.demo.core.persistence;

import com.example.demo.core.domain.model.entity.OutboxEvent;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM OutboxEvent e WHERE e.status = 'PENDING' ORDER BY e.createdAt LIMIT :limit")
    List<OutboxEvent> findPendingWithLock(@Param("limit") int limit);


    @Modifying
    @Query("""
        UPDATE OutboxEvent e
        SET e.status = com.example.demo.core.domain.model.enums.OutboxStatus.SENT,
            e.sentAt = :sentAt
        WHERE e.id IN :ids
        """)
    void markAsSent(@Param("ids") List<Long> ids, @Param("sentAt") LocalDateTime sentAt);




}
