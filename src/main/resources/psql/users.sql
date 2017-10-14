

CREATE TABLE IF NOT EXISTS users (
  id       SERIAL PRIMARY KEY,
  login    VARCHAR(50) UNIQUE,
  email    VARCHAR(50) UNIQUE,
  password TEXT,
  score    INTEGER DEFAULT 0
);

CREATE UNIQUE INDEX users_login_unique_idx
  ON users (login);

insert into users (id, login, email, password, score) VALUES (1, 'ps', 'ps@ps.ru', '111', 1);

SELECT * from users;