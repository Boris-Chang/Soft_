CREATE TABLE Soul_Report_Comment (
    Id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    Created_At TIMESTAMP WITH TIME ZONE NOT NULL,
    Comment_text TEXT NOT NULL,
    Soul_id BIGINT NOT NULL REFERENCES Souls(id)
);