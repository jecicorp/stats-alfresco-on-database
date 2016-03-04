-- NS_ID 1 = sys:
-- NS_ID 6 = cm:
select string_value
from alf_node_properties
where node_id = ?
and qname_id = (select id from alf_qname where ns_id=6 and local_name = 'name')