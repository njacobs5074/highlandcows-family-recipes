CREATE TABLE user_sessions(
    token text,
    user_id integer REFERENCES users,
    expiry timestamp with time zone,
    created timestamp with time zone,
    id serial PRIMARY KEY
);
