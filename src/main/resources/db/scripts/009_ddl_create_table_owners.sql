create table owners
(
    id        serial primary key,
    name      text,
    user_id int not null references auto_user(id)
);