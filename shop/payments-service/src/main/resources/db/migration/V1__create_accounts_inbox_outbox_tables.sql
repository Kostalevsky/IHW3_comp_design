CREATE TABLE accounts (
  user_id UUID PRIMARY KEY,
  balance NUMERIC(19, 2) NOT NULL DEFAULT 0,
  version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE inbox_messages (
  message_id UUID PRIMARY KEY,
  payload JSONB NOT NULL,
  status VARCHAR(20) NOT NULL,
  received_at TIMESTAMP NOT NULL DEFAULT NOW(),
  processed_at TIMESTAMP
);

CREATE TABLE outbox_events (
  event_id UUID PRIMARY KEY,
  payload JSONB NOT NULL,
  status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  processed_at TIMESTAMP
);