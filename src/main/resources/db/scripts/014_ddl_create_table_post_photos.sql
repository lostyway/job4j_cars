create table post_photos
(
    post_id  int references auto_post (id),
    photo_id int references photos (id),
    primary key (post_id, photo_id)
);