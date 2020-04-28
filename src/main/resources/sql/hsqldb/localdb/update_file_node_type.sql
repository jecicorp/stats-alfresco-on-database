-- Update DIR_SUM_SIZE for select node
update STATS_DIR_LOCAL_SIZE
set NODE_TYPE = 0
where DIR_SUM_SIZE is null