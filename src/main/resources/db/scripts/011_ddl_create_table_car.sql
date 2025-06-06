create table car
(
    id        serial primary key,
    mark_id   int references marks (id),
    model_id  int references models (id),
    car_year    text,
    engine_id int not null references engines (id)
);
