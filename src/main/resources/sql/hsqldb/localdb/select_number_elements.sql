-- Obtain number of files
SELECT COUNT(*)
FROM STATS_DIR_LOCAL_SIZE
WHERE PARENT_NODE_ID = ? AND NODE_TYPE = 0
