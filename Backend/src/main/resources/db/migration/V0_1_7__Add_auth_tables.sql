CREATE TABLE Users (
    Id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    Username VARCHAR(32) NOT NULL UNIQUE,
    Password_Hash VARCHAR(60) NOT NULL
);

CREATE TABLE Roles (
    Id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    Role_name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE User_Roles (
    User_id BIGINT NOT NULL REFERENCES Users(id),
    Role_id INTEGER NOT NULL REFERENCES Roles(id),
    PRIMARY KEY(User_id, Role_id)
);

INSERT INTO Roles (Role_name) VALUES
                                     ('ADMIN'),
                                     ('GOD'),
                                     ('DEVIL'),
                                     ('HEAVEN_ADVOCATE'),
                                     ('HEAVEN_PROSECUTOR'),
                                     ('SOULS_PROVIDER');
