// ----- PAGE FILTER INDEXES -----

CREATE INDEX page_type_idx IF NOT EXISTS
FOR (p:Page)
ON (p.pageType);


// ----- PAGE SEARCH INDEXES -----

CREATE INDEX page_name_idx IF NOT EXISTS
FOR (p:Page)
ON (p.name);


// ----- PAGE LOCATION INDEX -----

CREATE INDEX page_location_idx IF NOT EXISTS
FOR (p:Page)
ON (p.location);


// ----- PAGE TYPE INDEX -----

CREATE INDEX page_slug_idx IF NOT EXISTS
FOR (p:Page)
ON (p.slug);
