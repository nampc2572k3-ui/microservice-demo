// pre_migration_checks.cypher
// Run these on your Neo4j DB before applying UNIQUE constraints

// 1) Duplicate accId
MATCH (u:User) WHERE u.accId IS NOT NULL
WITH u.accId AS acc, count(*) AS c
WHERE c > 1
RETURN acc, c LIMIT 100;

// 2) Duplicate email
MATCH (u:User) WHERE u.email IS NOT NULL
WITH u.email AS email, count(*) AS c
WHERE c > 1
RETURN email, c LIMIT 100;

// 3) Check nulls for privacy_setting.section
MATCH (p:privacy_setting) WHERE p.section IS NULL RETURN count(*) AS nulls;

// 4) Cardinality sampling: top locations
MATCH (u:User) WITH u.location AS loc, count(*) AS c ORDER BY c DESC LIMIT 50 RETURN loc, c;

// 5) Count totals
MATCH (u:User) RETURN count(*) AS totalUsers;
MATCH (p:Page) RETURN count(*) AS totalPages;

// 6) Sample high-degree users (friend count via clusters)
// Counts friends by summing cluster sizes for each owner (approx)
MATCH (owner:User)<-[:CLUSTER_OF]-(c:friend_cluster)-[r:FRIEND]->() 
WITH owner, count(r) AS friendsApprox
ORDER BY friendsApprox DESC LIMIT 50
RETURN owner.slug, friendsApprox;
