INSERT INTO company (company_name, rating) values
('Luxoft', 'high'),
('GeeksForLess Inc.', 'middle'),
('Beetroot', 'low');

INSERT INTO developer(lastName, firstName, age, salary, company_id) values
('Chimadurov', 'Ruslan', 56, 1000, 1),
('Stepanova', 'Tetyana', 34, 1500, 2),
('Havrilitse', 'Vadim', 32, 2000, 3),
('Petrenko', 'Vladimir', 28, 1600, 2),
('Omelyashko', 'Olexey', 30, 1800, 1),
('Zerko', 'Andrew', 26, 1600, 3),
('Yakovenko', 'Vyacheslav', 24, 2500, 1);

INSERT INTO skill (language, level) values
('Java', 'junior'), ('Java', 'middle'), ('Java', 'senior'),
('JS', 'junior'), ('JS', 'middle'), ('JS', 'senior'),
('C++', 'junior'), ('C++', 'middle'), ('C++', 'senior'),
('PHP', 'junior'), ('PHP', 'middle'), ('PHP', 'senior');

INSERT INTO developer_skill (developer_id, skill_id) values
(1, 1),
(2, 1), (2, 4), (2, 10),
(3, 2), (3, 11),
(4, 2),
(5, 1), (5, 10),
(6, 2), (6, 8),
(7, 3), (7, 9), (7, 12);

INSERT INTO customer (customer_name,reputation) values
('IBM', 'trustworthy'),
('Apple', 'respectable'),
('Fasebook', 'insolvent'),
('Oracle', 'respectable');

INSERT INTO  project (project_name, company_id, customer_id, cost, start_date) values
('MetaUnivers', 1, 3, 15000, '2010-10-01'),
('Cristal Eye', 3, 2, 50000, '2015-06-01'),
('Super Technology', 2, 1, 40000, '2020-04-01'),
('Global DB', 1, 4, 25000, '2022-01-01'),
('Telecom Revolution', 3, 4, 33000, '2022-01-01');

INSERT INTO  project_developer (project_id, developer_id) values
(1, 1), (1,5),(1, 7),
(2, 3),
(3, 2), (3, 4),
(4, 1), (4, 7),
(5, 6);
