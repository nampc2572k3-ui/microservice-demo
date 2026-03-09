// ----- USER SEARCH INDEXES -----

CREATE INDEX user_name_idx IF NOT EXISTS
FOR (u:User)
ON (u.name);

CREATE INDEX user_first_last_name_idx IF NOT EXISTS
FOR (u:User)
ON (u.firstName, u.lastName);


// ----- USER LOCATION INDEXES -----

CREATE INDEX user_location_idx IF NOT EXISTS
FOR (u:User)
ON (u.location);

CREATE INDEX user_hometown_idx IF NOT EXISTS
FOR (u:User)
ON (u.hometown);

// ----- USER GENDER AND DOB INDEXES -----
CREATE INDEX user_gender_dob_idx IF NOT EXISTS
FOR (u:User)
ON (u.gender, u.dob);

CREATE INDEX user_accId_idx IF NOT EXISTS
FOR (u:User)
ON (u.accId);

CREATE INDEX user_slug_idx IF NOT EXISTS
FOR (u:User)
ON (u.slug);
