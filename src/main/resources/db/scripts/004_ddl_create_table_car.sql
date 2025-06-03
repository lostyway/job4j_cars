create table car
(
    id        serial primary key,
    name      text,
    category text,
    mark text,
    model text,
    engine_id int not null references engines(id)
);
