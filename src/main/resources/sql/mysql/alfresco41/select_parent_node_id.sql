select aca.child_node_id, aca.parent_node_id
from alf_child_assoc aca
where aca.child_node_id in ( :ids )