// ----- USER CONSTRAINTS -----

CREATE CONSTRAINT user_slug_unique IF NOT EXISTS
FOR (u:User)
REQUIRE u.slug IS UNIQUE;

// ----- PAGE CONSTRAINTS -----

CREATE CONSTRAINT page_slug_unique IF NOT EXISTS
FOR (p:Page)
REQUIRE p.slug IS UNIQUE;

// ----- GROUP CONSTRAINTS -----

CREATE CONSTRAINT group_slug_unique IF NOT EXISTS
FOR (g:Group)
REQUIRE g.slug IS UNIQUE;

// ----- PRIVACY SETTING INDEX (property existence constraint requires Enterprise) -----
// Replacing existence constraint with a normal index to be compatible with Community edition
CREATE INDEX privacy_section_idx IF NOT EXISTS
FOR (p:privacy_setting)
ON (p.section);
