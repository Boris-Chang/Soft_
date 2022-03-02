CREATE TABLE Thresholds(
  measurement_id BIGINT REFERENCES Measurements(id) PRIMARY KEY,
  value DOUBLE PRECISION NOT NULL
);

CREATE TABLE Threshold_Alerts(
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    measurement_id BIGINT REFERENCES Measurements(id),
    text TEXT NOT NULL
);