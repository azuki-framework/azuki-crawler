-- Database: db_crawler

-- DROP DATABASE db_crawler;

CREATE DATABASE db_crawler
  WITH OWNER = crawler
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'Japanese_Japan.932'
       LC_CTYPE = 'Japanese_Japan.932'
       CONNECTION LIMIT = -1;
