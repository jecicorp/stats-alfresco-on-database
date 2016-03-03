package fr.jeci.alfresco.saod.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class LocalDaoImpl implements LocalDao {
	static final Logger LOG = LoggerFactory.getLogger(LocalDaoImpl.class);

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(@Qualifier("localDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Autowired
	private SqlQueries sqlQueries;

	private String insertStatsDirLocalSize;
	private String updateParentNodeId;

	@PostConstruct
	public void afterPropertiesSet() throws Exception {
		insertStatsDirLocalSize = sqlQueries.getQuery("insert_stats_dir_local_size.sql", true);
		updateParentNodeId = sqlQueries.getQuery("update_parent_node_id.sql", true);
	}

	@Override
	public void insertStatsDirLocalSize(Map<Long, Long> dirLocalSize) {
		List<Object[]> batchArgs = new ArrayList<>();

		for (Entry<Long, Long> e : dirLocalSize.entrySet()) {
			batchArgs.add(new Object[] { e.getKey(), e.getValue() });
		}

		this.jdbcTemplate.batchUpdate(insertStatsDirLocalSize, batchArgs);
	}

	@Override
	public void updateParentNodeId(Map<Long, Long> nodeids) {
		List<Object[]> batchArgs = new ArrayList<>();

		for (Entry<Long, Long> e : nodeids.entrySet()) {
			batchArgs.add(new Object[] { e.getKey(), e.getValue() });
		}

		this.jdbcTemplate.batchUpdate(updateParentNodeId, batchArgs);

	}
}
