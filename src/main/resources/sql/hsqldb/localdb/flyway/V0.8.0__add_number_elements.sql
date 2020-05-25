-- 0 : is a file
-- else : is a directory with a number of element
ALTER TABLE PUBLIC.STATS_DIR_LOCAL_SIZE
ADD NUMBER_ELEMENTS BIGINT DEFAULT 0 ;
