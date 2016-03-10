-- NS_ID 1 = sys:
-- NS_ID 6 = cm:
select  protocol, identifier, alf_node.uuid
from alf_node
join alf_store on alf_store.id = alf_node.store_id
where alf_node.id = ?