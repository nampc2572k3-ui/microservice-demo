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

// ----- PRIVACY SETTING CONSTRAINTS -----

CREATE CONSTRAINT privacy_section_unique IF NOT EXISTS
FOR (p:privacy_setting)
REQUIRE (p.section) IS NOT NULL