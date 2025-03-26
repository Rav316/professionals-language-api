--liquibase formatted sql

--changeset alex:1

CREATE TABLE leaderboard_item
(
    user_id int REFERENCES users(id) ON DELETE CASCADE PRIMARY KEY ,
    score int NOT NULL DEFAULT 0
)