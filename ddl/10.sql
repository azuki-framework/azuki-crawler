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
