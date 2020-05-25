<<<<<<< HEAD
-- Update NUMBER_ELEMENT for select node
update STATS_DIR_LOCAL_SIZE a
set a.NUMBER_ELEMENT = (
	select sum( isnull( b.NUMBER_ELEMENT, 0 ) ) + sum( isnull( b.NUMBER_ELEMENT, 0 ) )
	from STATS_DIR_LOCAL_SIZE b
	where b.PARENT_NODE_ID = a.NODE_ID
)
where a.NODE_ID = :ids
=======
-- Update DIR_SUM_SIZE for select node
update STATS_DIR_LOCAL_SIZE
set NUMBER_ELEMENTS = :number_elements
where NODE_ID = :node_id
>>>>>>> 27c1d0dd062e3b7eb8899d61d876e97e8ab0d735
