-- Update NUMBER_SUM_ELEMENTS for select node
update STATS_DIR_LOCAL_SIZE a
set a.NUMBER_SUM_ELEMENTS = (
	select sum( isnull( b.NUMBER_LOCAL_ELEMENTS, 0 ) ) + sum( isnull( b.NUMBER_SUM_ELEMENTS, 0 ) )
	from STATS_DIR_LOCAL_SIZE b
	where b.PARENT_NODE_ID = a.NODE_ID
)
where a.NODE_ID = :ids

