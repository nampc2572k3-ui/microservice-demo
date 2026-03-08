// ----- Friend  -----

CREATE INDEX friend_cluster_shard
FOR (c:friend_cluster)
ON (c.shard)


// ----- follow -----
CREATE INDEX follow_cluster_shard
FOR (c:follow_cluster)
ON (c.shard)