create table photos
(
    id serial primary key,
    name text not null,
    path text not null,
    post_id int not null references auto_post(id) on delete cascade
);