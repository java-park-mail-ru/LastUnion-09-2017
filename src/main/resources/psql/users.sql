

CREATE TABLE IF NOT EXISTS users (
  id       SERIAL PRIMARY KEY,
  username    VARCHAR(50) UNIQUE,
  useremail    VARCHAR(50) UNIQUE,
  userpassword TEXT,
  userscore    INTEGER DEFAULT 0
);

CREATE UNIQUE INDEX users_username_unique_idx
  ON users (username);

