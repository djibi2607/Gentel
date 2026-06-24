CREATE TABLE users (
                       id              BIGSERIAL PRIMARY KEY,
                       name            VARCHAR(255) NOT NULL,
                       email           VARCHAR(255),
                       phone           VARCHAR(255),
                       birth_date      DATE NOT NULL,
                       password        VARCHAR(255) NOT NULL,
                       fa_enabled      BOOLEAN NOT NULL DEFAULT FALSE,
                       is_deleted      BOOLEAN NOT NULL DEFAULT FALSE,
                       created_at      TIMESTAMP WITH TIME ZONE,
                       updated_at      TIMESTAMP WITH TIME ZONE
);

CREATE TABLE balance (
                         id              BIGSERIAL PRIMARY KEY,
                         balance         NUMERIC(10,2) NOT NULL DEFAULT 10000,
                         wallet_user     BIGINT NOT NULL UNIQUE,
                         CONSTRAINT fk_wallet_user FOREIGN KEY (wallet_user) REFERENCES users(id)
);

CREATE TABLE hotels (
                        id                  BIGSERIAL PRIMARY KEY,
                        name                VARCHAR(255) NOT NULL,
                        address             VARCHAR(255) NOT NULL,
                        one_room            BIGINT NOT NULL,
                        two_rooms           BIGINT NOT NULL,
                        more_rooms          BIGINT NOT NULL,
                        deleted             BOOLEAN NOT NULL DEFAULT FALSE,
                        ratings             INTEGER,
                        default_check_in    TIME,
                        default_check_out   TIME,
                        created_at          TIMESTAMP WITH TIME ZONE,
                        updated_at          TIMESTAMP WITH TIME ZONE
);

CREATE TABLE rooms (
                       id              BIGSERIAL PRIMARY KEY,
                       room_number     VARCHAR(255) NOT NULL,
                       room_type       VARCHAR(50) NOT NULL,
                       night_price     NUMERIC(10,2) NOT NULL,
                       available       BOOLEAN NOT NULL DEFAULT TRUE,
                       room_hotel      BIGINT,
                       created_at      TIMESTAMP WITH TIME ZONE,
                       updated_at      TIMESTAMP WITH TIME ZONE,
                       CONSTRAINT fk_room_hotel FOREIGN KEY (room_hotel) REFERENCES hotels(id)
);

CREATE TABLE bookings (
                          id              BIGSERIAL PRIMARY KEY,
                          total           NUMERIC(7,2) NOT NULL,
                          note            VARCHAR(255),
                          active          BOOLEAN NOT NULL DEFAULT TRUE,
                          booking_user    BIGINT,
                          booking_room    BIGINT,
                          check_in_date   DATE NOT NULL,
                          check_out_date  DATE NOT NULL,
                          booked_at       TIMESTAMP WITH TIME ZONE,
                          updated_at      TIMESTAMP WITH TIME ZONE,
                          CONSTRAINT fk_booking_user FOREIGN KEY (booking_user) REFERENCES users(id),
                          CONSTRAINT fk_booking_room FOREIGN KEY (booking_room) REFERENCES rooms(id)
);