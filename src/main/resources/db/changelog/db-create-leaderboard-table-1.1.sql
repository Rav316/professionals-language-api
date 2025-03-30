--liquibase formatted sql

--changeset alex:1

ALTER TABLE leaderboard_item
ALTER COLUMN score
SET DATA TYPE DOUBLE PRECISION
USING score::DOUBLE PRECISION;
