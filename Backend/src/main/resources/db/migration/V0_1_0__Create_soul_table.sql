CREATE TABLE Souls (
    Id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    First_Name VARCHAR(50) NOT NULL,
    Last_Name VARCHAR(50) NOT NULL,
    Date_of_death TIMESTAMP NOT NULL
);