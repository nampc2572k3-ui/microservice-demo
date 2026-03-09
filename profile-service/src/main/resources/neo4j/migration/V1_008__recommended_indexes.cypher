// V1_008__recommended_indexes.cypher
// Recommended additional indexes (safe, non-unique) to improve common lookups.

// Indexes for common lookup properties
CREATE INDEX user_email_idx IF NOT EXISTS
FOR (u:User)
ON (u.email);

CREATE INDEX user_phone_idx IF NOT EXISTS
FOR (u:User)
ON (u.phone);

// Fast lookup by external accId (if not creating UNIQUE constraint)
CREATE INDEX user_accId_idx IF NOT EXISTS
FOR (u:User)
ON (u.accId);

// Cluster owner lookup: if you add ownerAccId to cluster nodes for faster lookup
CREATE INDEX friend_cluster_owner_idx IF NOT EXISTS
FOR (c:friend_cluster)
ON (c.ownerAccId);

CREATE INDEX follow_cluster_owner_idx IF NOT EXISTS
FOR (c:follow_cluster)
ON (c.ownerAccId);

// Relationship property indexes (useful if you add createdAt or timestamp to relationship props)
// Example: index createdAt on FRIEND / FOLLOW relationships
CREATE INDEX friend_created_at_idx IF NOT EXISTS
FOR ()-[r:FRIEND]-()
ON (r.createdAt);

CREATE INDEX follow_created_at_idx IF NOT EXISTS
FOR ()-[r:FOLLOW]-()
ON (r.createdAt);
