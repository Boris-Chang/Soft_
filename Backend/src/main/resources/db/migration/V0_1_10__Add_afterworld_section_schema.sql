CREATE TYPE Afterworld_Kind as ENUM ('HELL', 'PARADISE');

CREATE table Soul_Afterworld_Location (
    soul_id BIGINT REFERENCES Souls(id) PRIMARY KEY,
    kind Afterworld_Kind NOT NULL,
    section_number INTEGER NOT NULL
);