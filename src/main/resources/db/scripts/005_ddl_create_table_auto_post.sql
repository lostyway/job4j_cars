create table auto_post
(
    id           serial primary key,
    description  text                          not null,
    created      timestamp,
    is_sold      boolean                       not null,
    photo_url    text,
    auto_user_id int references auto_user (id) not null,
    car_id       int references car (id)
);