-- Update DIR_SUM_SIZE for select node
update STATS_DIR_LOCAL_SIZE a
set a.DIR_SUM_SIZE = (
	select sum( isnull( b.DIR_LOCAL_SIZE, 0 ) ) + sum( isnull( b.DIR_SUM_SIZE, 0 ) )
	from STATS_DIR_LOCAL_SIZE b
	where b.PARENT_NODE_ID = a.NODE_ID
)
where a.NODE_ID = :ids