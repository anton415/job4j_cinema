INSERT INTO files (id, name, path)
VALUES (1, 'matrix.svg', 'files/matrix.svg'),
       (2, 'interstellar.svg', 'files/interstellar.svg'),
       (3, 'inception.svg', 'files/inception.svg');

-- Rollback:
-- DELETE FROM files WHERE id IN (1, 2, 3);
