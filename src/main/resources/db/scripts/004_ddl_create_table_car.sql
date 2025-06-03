create table car
(
    id        serial primary key,
    name      text,
    category text,
    mark text,
    model text,
    year date,
    engine_id int not null references engines(id)
);
