package com.example.demo.core.domain.model.entity;

import com.example.demo.core.domain.model.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventId;
    private String topic;
    private String payloadKey;

    @Column(columnDefinition = "jsonb")
    private String payload;        // JSON serialized

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;   // PENDING, SENT

    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

}
