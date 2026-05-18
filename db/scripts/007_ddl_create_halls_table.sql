CREATE TABLE halls
(
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    row_count INT NOT NULL,
    place_count INT NOT NULL,
    description VARCHAR NOT NULL
);
