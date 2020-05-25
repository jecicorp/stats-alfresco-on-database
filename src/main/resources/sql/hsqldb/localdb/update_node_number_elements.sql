-- Update NUMBER_ELEMENT for select node
update STATS_DIR_LOCAL_SIZE a
set a.NUMBER_ELEMENT = (
	select sum( isnull( b.NUMBER_ELEMENT, 0 ) ) + sum( isnull( b.NUMBER_ELEMENT, 0 ) )
	from STATS_DIR_LOCAL_SIZE b
	where b.PARENT_NODE_ID = a.NODE_ID
)
where a.NODE_ID = :ids