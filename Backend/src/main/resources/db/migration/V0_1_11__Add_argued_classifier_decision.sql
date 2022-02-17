CREATE TYPE Argued_by_kind as ENUM ('GOD', 'DEVIL');

CREATE TABLE Argued_classifier_decision(
   soul_id BIGINT REFERENCES Souls(id),
   argued_by Argued_by_kind NOT NULL,
   afterworld_kind Afterworld_Kind,
   section_number INTEGER,
   CHECK ((afterworld_kind IS NULL) = (section_number IS NULL)),
   PRIMARY KEY (soul_id, argued_by)
);
