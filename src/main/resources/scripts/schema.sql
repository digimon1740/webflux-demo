DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id         bigint NOT NULL AUTO_INCREMENT,
    username   varchar(50),
    password   varchar(300),
    created_at timestamp default NOW(),
    updated_at timestamp default NOW(),
    primary key (id)
);

DROP TABLE IF EXISTS contents;
CREATE TABLE contents
(
    id         bigint NOT NULL AUTO_INCREMENT,
    user_id    bigint NOT NULL,
    memo       varchar(1000),
    created_at timestamp default NOW(),
    updated_at timestamp default NOW(),
    primary key (id)
);