-- Update DIR_SUM_SIZE for select node
update STATS_DIR_LOCAL_SIZE
set NUMBER_ELEMENTS = :number_elements
where NODE_ID = :node_id