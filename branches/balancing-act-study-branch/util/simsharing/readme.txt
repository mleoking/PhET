Mongodb must be running on the server, should be launched with:
/home/phet/mongodb-linux-i686-1.6.5/bin/mongod

Here is how I found that path:
find / -name mongod 2>/dev/null
/home/phet/mongodb-linux-i686-1.6.5/bin/mongod

The database resides in /data/db, this may need to be cleared if you start having memory problems reported by server app