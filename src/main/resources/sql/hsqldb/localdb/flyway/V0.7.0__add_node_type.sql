-- 0 : is a file
-- 1 : is a directory
ALTER TABLE PUBLIC.STATS_DIR_LOCAL_SIZE
<<<<<<< HEAD
ADD NODE_TYPE TINYINT DEFAULT 1;
=======
ADD NODE_TYPE TINYINT DEFAULT 1;

-- 0 : is a file
-- else : is a directory with a number of element
ALTER TABLE PUBLIC.STATS_DIR_LOCAL_SIZE
ADD NUMBER_ELEMENTS BIGINT DEFAULT 0 ;
>>>>>>> 27c1d0dd062e3b7eb8899d61d876e97e8ab0d735
