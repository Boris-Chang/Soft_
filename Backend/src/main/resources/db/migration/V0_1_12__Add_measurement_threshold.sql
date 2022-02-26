CREATE TABLE Thresholds(
  measurement_id BIGINT REFERENCES Measurements(id) PRIMARY KEY,
  value DOUBLE PRECISION NOT NULL
);

CREATE TABLE ThresholdAlerts(
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    measurement_id BIGINT REFERENCES Measurements(id),
    text TEXT NOT NULL
);