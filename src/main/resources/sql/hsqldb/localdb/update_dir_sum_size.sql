-- Upadte DIR_SUM_SIZE for select node
update STATS_DIR_LOCAL_SIZE a
set a.DIR_SUM_SIZE = (
	select sum(b.DIR_LOCAL_SIZE)
	from STATS_DIR_LOCAL_SIZE b
	where b.PARENT_NODE_ID = a.NODE_ID
)
where a.NODE_ID in ( :ids )