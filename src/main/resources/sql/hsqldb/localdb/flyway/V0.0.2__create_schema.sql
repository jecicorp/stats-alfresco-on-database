CREATE TABLE STATS_DIR_LOCAL_SIZE (
  node_id bigint NOT NULL,
  parent_node_id bigint,
  dir_local_size bigint,
  dir_sum_size bigint,
  CONSTRAINT STATS_DIR_LOCAL_SIZE_PK PRIMARY KEY (node_id)
);

CREATE INDEX sdls_parent_node_id_idx ON STATS_DIR_LOCAL_SIZE (parent_node_id);