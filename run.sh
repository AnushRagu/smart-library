#!/bin/sh
set -eu
MYSQL_CONNECTOR_JAR="${MYSQL_CONNECTOR_JAR:-lib/mysql-connector-j-9.5.0.jar}"
if [ ! -f "$MYSQL_CONNECTOR_JAR" ]; then
  echo "Set MYSQL_CONNECTOR_JAR to the MySQL Connector/J JAR path." >&2
  exit 1
fi
mkdir -p out
find src/main/java -name '*.java' -print > out/sources.list
javac -encoding UTF-8 -cp "$MYSQL_CONNECTOR_JAR" -d out @out/sources.list
exec java -cp "out:$MYSQL_CONNECTOR_JAR" smartlibrary.Main
