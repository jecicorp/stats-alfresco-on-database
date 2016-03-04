select NODE_ID, PARENT_NODE_ID, DIR_LOCAL_SIZE, DIR_SUM_SIZE
from stats_dir_local_size
where NODE_ID = ?