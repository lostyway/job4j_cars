CREATE TABLE participates
(
    user_id INT REFERENCES auto_user (id),
    post_id INT REFERENCES auto_post (id),
    UNIQUE (user_id, post_id)
);
