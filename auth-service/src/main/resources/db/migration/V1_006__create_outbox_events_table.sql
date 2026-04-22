CREATE TABLE outbox_events
(
    id          BIGSERIAL PRIMARY KEY,
    event_id    VARCHAR(36)  NOT NULL UNIQUE,
    topic       VARCHAR(255) NOT NULL,
    payload_key VARCHAR(255) NOT NULL,
    payload     JSONB        NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMP    NOT NULL DEFAULT now(),
    sent_at     TIMESTAMP
);
CREATE INDEX idx_outbox_status ON outbox_events (status, created_at);