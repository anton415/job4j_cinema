CREATE TABLE genres
(
    id SERIAL PRIMARY KEY,
    name VARCHAR UNIQUE NOT NULL
);

-- Rollback:
-- DROP TABLE IF EXISTS genres;
