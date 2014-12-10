-- Table: tm_content

-- DROP TABLE tm_content;

CREATE TABLE tm_content
(
  content_id character(32) NOT NULL,
  content_areas text,
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
