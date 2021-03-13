CREATE TABLE family_recipe_instances(
  name text UNIQUE NOT NULL,
  description text NOT NULL,
  admin_id integer references users ON DELETE CASCADE,
  created timestamp with time zone,
  id serial PRIMARY KEY
);

ALTER TABLE users ADD COLUMN family_recipe_instance_id integer references family_recipe_instances ON DELETE CASCADE;
