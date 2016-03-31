select string_value
from alf_node_properties
where node_id = ?
and qname_id = (
	SELECT alf_qname.id 
	FROM alf_qname
	JOIN alf_namespace on alf_namespace.id = alf_qname.NS_ID
	WHERE  local_name = 'name'
	AND uri = 'http://www.alfresco.org/model/content/1.0'
)