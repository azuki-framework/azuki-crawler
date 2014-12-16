-- Role: crawler

-- DROP ROLE crawler;

CREATE ROLE crawler LOGIN
  ENCRYPTED PASSWORD 'md5207774cee4f587be92b25aac56d6bead'
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;

-- Database: db_crawler

-- DROP DATABASE db_crawler;

CREATE DATABASE db_crawler
  WITH OWNER = crawler
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'Japanese_Japan.932'
       LC_CTYPE = 'Japanese_Japan.932'
       CONNECTION LIMIT = -1;

       
       
-- Table: tm_host

-- DROP TABLE tm_host;

CREATE TABLE tm_host
(
  host_id character(32) NOT NULL,
  host_name character varying(256) NOT NULL,
  host_protocol character varying(64) NOT NULL DEFAULT 'http'::character varying,
  host_port integer NOT NULL DEFAULT 80,
  status integer NOT NULL DEFAULT 0,
  last_access_date timestamp with time zone,
  create_date timestamp with time zone NOT NULL,
  update_date timestamp with time zone NOT NULL,
  delete_date timestamp with time zone,
  CONSTRAINT tm_host_pk PRIMARY KEY (host_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tm_host
  OWNER TO crawler;

  
-- Table: tm_content

-- DROP TABLE tm_content;

CREATE TABLE tm_content
(
  content_id character(32) NOT NULL,
  content_areas text,
  content_path text,
  content_type text,
  content_length bigint NOT NULL DEFAULT (-1),
  status integer NOT NULL DEFAULT 0,
  result_code integer NOT NULL DEFAULT (-1),
  host_id character(32),
  create_date timestamp with time zone NOT NULL,
  update_date timestamp with time zone NOT NULL,
  delete_date timestamp with time zone,
  CONSTRAINT tm_content_pk PRIMARY KEY (content_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tm_content
  OWNER TO crawler;

  
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
  OWNER TO crawler;
