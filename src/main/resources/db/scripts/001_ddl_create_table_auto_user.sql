create table if not exists auto_user
(
    id       serial primary key,
    login    text not null,
    password text not null
);