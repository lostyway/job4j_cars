create table car
(
    id        serial primary key,
    name      text,
    mark_id int references marks(id),
    model_id int references models(id),
    year date,
    engine_id int not null references engines(id)
);
