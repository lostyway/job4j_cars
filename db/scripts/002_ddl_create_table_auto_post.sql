create table if not exists auto_post
(
  id int primary key,
  description text not null,
  created timestamp,
  auto_user_id int references auto_user(id) not null
);