// ----- GROUP SEARCH INDEX -----

CREATE INDEX group_name_idx IF NOT EXISTS
FOR (g:Group)
ON (g.name);

CREATE INDEX group_slug_idx IF NOT EXISTS FOR (g:Group) ON (g.slug);