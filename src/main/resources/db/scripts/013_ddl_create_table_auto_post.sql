create table auto_post
(
    id           serial primary key,
    description  text                          not null,
    created      timestamp                     not null,
    price       bigint                        not null,
    is_sold      boolean                       not null,
    auto_user_id int references auto_user (id) not null,
    car_id       int references car (id)       not null
);