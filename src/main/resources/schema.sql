CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  registration_date TIMESTAMP,
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
   id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
   user_id BIGINT,
   name VARCHAR(255) NOT NULL,
   description VARCHAR NOT NULL,
   available BOOLEAN NOT NULL,
   CONSTRAINT fk_items_to_users FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS bookings (
   id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
   booker_id BIGINT,
   item_id BIGINT,
   create_date TIMESTAMP NOT NULL,
   start_date TIMESTAMP NOT NULL,
   end_date TIMESTAMP NOT NULL,
   status VARCHAR(50) NOT NULL,
   CONSTRAINT fk_bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(id),
   CONSTRAINT fk_bookings_to_items FOREIGN KEY(item_id) REFERENCES items(id),
   UNIQUE(booker_id, item_id)
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
  text VARCHAR(512) NOT NULL,
  item_id BIGINT,
  author_id BIGINT,
  created_date TIMESTAMP,
  CONSTRAINT fk_comments_to_users FOREIGN KEY(author_id) REFERENCES users(id),
  CONSTRAINT fk_comments_to_items FOREIGN KEY(item_id) REFERENCES items(id),
  UNIQUE(item_id, author_id)
);