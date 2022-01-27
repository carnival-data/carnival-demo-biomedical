FROM postgres

COPY data/db/setup.sql 	/docker-entrypoint-initdb.d/setup.sql
COPY data/db/*.csv		/db/
