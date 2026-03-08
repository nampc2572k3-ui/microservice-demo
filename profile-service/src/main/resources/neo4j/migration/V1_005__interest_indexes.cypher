// ----- HOBBY -----

CREATE INDEX hobby_name_idx IF NOT EXISTS
FOR (h:Hobby)
ON (h.name);


// ----- PLACE -----

CREATE INDEX place_name_idx IF NOT EXISTS
FOR (p:Place)
ON (p.name);


// ----- MEDIA -----

CREATE INDEX media_type_idx IF NOT EXISTS
FOR (m:Media)
ON (m.mediaType);


// ----- MUSIC -----

CREATE INDEX music_name_idx IF NOT EXISTS
FOR (m:Music)
ON (m.name);

CREATE INDEX music_author_idx IF NOT EXISTS
FOR (m:Music)
ON (m.author);