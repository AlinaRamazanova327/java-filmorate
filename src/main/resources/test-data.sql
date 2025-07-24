MERGE INTO mpa (id, title) VALUES
(1, 'G'),
(2,'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

MERGE INTO genres (id, genre) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

MERGE INTO users (email, login, name, birthday) VALUES
(1, 'example@example.com'),
(2, 'login'),
(3, 'name'),
(4, '2002-02-02');

MERGE INTO users (email, login, name, birthday) VALUES
(1, 'email@email.com'),
(2, 'login2'),
(3, 'name2'),
(4, '2000-02-02');

MERGE INTO films (title, description, duration, release_date, mpa) VALUES
('film', 'desc', 111, '2002-02-20', 3);

MERGE INTO films (title, description, duration, release_date, mpa) VALUES
('film2', 'desc2', 120, '2000-02-20', 2);