FROM postgres

COPY db/setup.sql 	/docker-entrypoint-initdb.d/setup.sql
COPY db/*.csv		/db/
