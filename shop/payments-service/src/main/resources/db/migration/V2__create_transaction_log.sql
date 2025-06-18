CREATE TABLE transaction_log (
    tx_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    processed_at TIMESTAMP
);