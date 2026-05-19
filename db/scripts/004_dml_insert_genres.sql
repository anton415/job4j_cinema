INSERT INTO genres (id, name)
VALUES (1, 'Фантастика'),
       (2, 'Драма'),
       (3, 'Триллер');

-- Rollback:
-- DELETE FROM genres WHERE id IN (1, 2, 3);
