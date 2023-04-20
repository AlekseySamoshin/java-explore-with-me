DROP TABLE IF EXISTS hits;

CREATE TABLE IF NOT EXISTS hits (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  app VARCHAR(255) NOT NULL,
  uri VARCHAR(255) NOT NULL,
  ip VARCHAR(16) NOT NULL,
  datetime TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT pk_hit PRIMARY KEY (id)
);