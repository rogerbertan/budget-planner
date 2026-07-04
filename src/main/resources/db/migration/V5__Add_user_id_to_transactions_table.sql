ALTER TABLE transactions
    ADD COLUMN user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE;

CREATE INDEX idx_transactions_user_id ON transactions(user_id);