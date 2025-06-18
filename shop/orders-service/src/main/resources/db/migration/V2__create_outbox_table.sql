CREATE TABLE outbox_events (
  event_id UUID PRIMARY KEY,
  payload JSONB NOT NULL,
  status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT now()
);