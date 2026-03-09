// ----- Friend  -----

CREATE INDEX friend_cluster_shard IF NOT EXISTS
FOR (c:friend_cluster)
ON (c.shard);


// ----- follow -----
CREATE INDEX follow_cluster_shard IF NOT EXISTS
FOR (c:follow_cluster)
ON (c.shard);
