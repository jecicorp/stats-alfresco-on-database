select sdls.node_id
from stats_dir_local_size sdls
where sdls.parent_node_id = ?