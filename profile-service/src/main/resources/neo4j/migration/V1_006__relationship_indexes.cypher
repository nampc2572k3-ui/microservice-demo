// ----- WORK RELATIONSHIP -----

CREATE INDEX work_start_date_idx IF NOT EXISTS
FOR ()-[r:WORK_AT]-()
ON (r.startDate);


// ----- STUDY RELATIONSHIP -----

CREATE INDEX study_start_date_idx IF NOT EXISTS
FOR ()-[r:STUDY_AT]-()
ON (r.startDate);

CREATE INDEX study_end_date_idx IF NOT EXISTS
FOR ()-[r:STUDY_AT]-()
ON (r.endDate);