#!/bin/bash

echo "⏳ Waiting for master..."

until mysql -h mysql-master -uroot -proot -e "SELECT 1" > /dev/null 2>&1
do
  sleep 2
done

echo "⏳ Waiting for replica user..."

until mysql -h mysql-master -ureplica -p123456 -e "SELECT 1" > /dev/null 2>&1
do
  sleep 2
done

echo "✅ Configuring replication..."

mysql -uroot -proot <<EOF

STOP SLAVE;

CHANGE MASTER TO
  MASTER_HOST='mysql-master',
  MASTER_USER='replica',
  MASTER_PASSWORD='123456',
  MASTER_AUTO_POSITION=1;

START SLAVE;

EOF

echo "🎉 Replication configured!"