CREATE TABLE users(
    username text UNIQUE NOT NULL,
    password text NOT NULL,
    created timestamp with time zone,
    id serial PRIMARY KEY
);
