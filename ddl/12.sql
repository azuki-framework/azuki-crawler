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
