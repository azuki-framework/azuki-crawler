-- Role: user

-- DROP ROLE "user";

CREATE ROLE "user" LOGIN
  ENCRYPTED PASSWORD 'md55d9c68c6c50ed3d02a2fcf54f63993b6'
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;

-- Database: db_crawler

-- DROP DATABASE db_crawler;

CREATE DATABASE db_crawler
  WITH OWNER = "user"
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'C'
       LC_CTYPE = 'C'
       CONNECTION LIMIT = -1;

-- Table: tm_host

-- DROP TABLE tm_host;

CREATE TABLE tm_host
(
  host_id character(32) NOT NULL,
  host_name character varying(256),
  last_access_date timestamp with time zone,
  status integer NOT NULL DEFAULT 0,
  CONSTRAINT tm_host_pk PRIMARY KEY (host_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tm_host
  OWNER TO "user";

-- Table: tm_content

-- DROP TABLE tm_content;

CREATE TABLE tm_content
(
  content_id character(32) NOT NULL,
  content_areas text,
  content_type text,
  content_length bigint NOT NULL DEFAULT (-1),
  host_id character(32),
  status integer NOT NULL DEFAULT 0,
  result_code integer NOT NULL DEFAULT (-1),
  CONSTRAINT tm_content_pk PRIMARY KEY (content_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tm_content
  OWNER TO "user";

-- Table: td_content_parse

-- DROP TABLE td_content_parse;

CREATE TABLE td_content_parse
(
  content_parse_id character(32) NOT NULL,
  content_id character(32),
  status integer DEFAULT 0,
  CONSTRAINT td_content_parse_pk PRIMARY KEY (content_parse_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE td_content_parse
  OWNER TO "user";
