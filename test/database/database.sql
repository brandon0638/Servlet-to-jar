CREATE DATABASE entreprise;

USE entreprise;


CREATE TABLE employee (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL
);


INSERT INTO employee(nom) VALUES
('Brandon'),
('Alice'),
('Jean');
