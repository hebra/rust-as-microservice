-- Add migration script here
CREATE TABLE users
(
    userid         VARCHAR(100) PRIMARY KEY,
    email          VARCHAR(255) NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,
    terms_accepted BOOLEAN      NOT NULL DEFAULT false
);
