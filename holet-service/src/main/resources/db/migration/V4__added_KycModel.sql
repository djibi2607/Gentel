CREATE TABLE kycs (
                      id              BIGSERIAL PRIMARY KEY,
                      kyc_type        VARCHAR(50) NOT NULL,
                      kyc_status      VARCHAR(50) DEFAULT 'Pending',
                      url             VARCHAR(255),
                      remainder       BOOLEAN NOT NULL DEFAULT FALSE,
                      kyc_user        BIGINT,
                      created_at      TIMESTAMP WITH TIME ZONE,
                      updated_at      TIMESTAMP WITH TIME ZONE,
                      CONSTRAINT fk_kyc_user FOREIGN KEY (kyc_user) REFERENCES users(id)
);
