-- 0 : is a file
-- 1 : is a directory
ALTER TABLE PUBLIC.STATS_DIR_LOCAL_SIZE ADD NODE_TYPE TINYINT DEFAULT 1 ;