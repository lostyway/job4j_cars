create table if not exists auto_user
(
    id       int primary key,
    login    text not null,
    password text not null
);