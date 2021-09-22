CREATE TYPE Surveys_Addresses as ENUM (
    --Пыточники
    'TORTURERS',
    --Грешник
    'SINNERS'
);

CREATE TABLE Surveys (
    Id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    Title VARCHAR(255) NOT NULL,
    Url VARCHAR(2000) NOT NULL,
    Addressee Surveys_Addresses NOT NULL
);