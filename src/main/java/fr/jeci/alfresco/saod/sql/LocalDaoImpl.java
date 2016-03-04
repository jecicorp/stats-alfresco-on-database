package fr.jeci.alfresco.saod.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import fr.jeci.alfresco.saod.SaodException;
import fr.jeci.alfresco.saod.pojo.PrintNode;

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

	@Override
	public void initDatabase() throws SaodException {
		this.jdbcTemplate.execute(sqlQueries.getQuery("schema.sql", true));
	}

	@Override
	public void insertStatsDirLocalSize(Map<Long, Long> dirLocalSize) throws SaodException {
		NamedParameterJdbcTemplate jdbcNamesTpl = new NamedParameterJdbcTemplate(this.jdbcTemplate);

		List<MapSqlParameterSource> batchArgs = new ArrayList<>();

		for (Entry<Long, Long> e : dirLocalSize.entrySet()) {
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("node_id", e.getKey());
			parameters.addValue("local_size", e.getValue());
			batchArgs.add(parameters);
		}

		String query = sqlQueries.getQuery("insert_stats_dir_local_size.sql", true);
		jdbcNamesTpl.batchUpdate(query, batchArgs.toArray(new MapSqlParameterSource[dirLocalSize.size()]));
	}

	@Override
	public void insertStatsDirNoSize(List<Long> parentsid) throws SaodException {
		NamedParameterJdbcTemplate jdbcNamesTpl = new NamedParameterJdbcTemplate(this.jdbcTemplate);

		String query = sqlQueries.getQuery("insert_stats_dir_local_size.sql", true);

		for (Long id : parentsid) {
			if (loadRow(id) != null) {
				continue;
			}

			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("node_id", id);
			parameters.addValue("local_size", 0);
			jdbcNamesTpl.update(query, parameters);
		}
	}

	}

	@Override
	public void updateParentNodeId(Map<Long, Long> nodeids) throws SaodException {
		NamedParameterJdbcTemplate jdbcNamesTpl = new NamedParameterJdbcTemplate(this.jdbcTemplate);

		List<MapSqlParameterSource> batchArgs = new ArrayList<>();

		for (Entry<Long, Long> e : nodeids.entrySet()) {
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("node_id", e.getKey());
			parameters.addValue("parent_node_id", e.getValue());
			batchArgs.add(parameters);
		}

		String query = sqlQueries.getQuery("update_parent_node_id.sql", true);
		jdbcNamesTpl.batchUpdate(query, batchArgs.toArray(new MapSqlParameterSource[nodeids.size()]));
	}

	@Override
	public List<Long> selectRootFolders() throws SaodException {
		String query = sqlQueries.getQuery("select_root_folders.sql", true);
		final SqlRowSet queryForRowSet = this.jdbcTemplate.queryForRowSet(query);

		final List<Long> ids = new ArrayList<>();
		while (queryForRowSet.next()) {
			ids.add(queryForRowSet.getLong(1));
		}

		return ids;
	}

	@Override
	public List<Long> selectSubFolders(Long nodeid) throws SaodException {
		String query = sqlQueries.getQuery("select_sub_folders.sql", true);
		final SqlRowSet queryForRowSet = this.jdbcTemplate.queryForRowSet(query, nodeid);

		final List<Long> ids = new ArrayList<>();
		while (queryForRowSet.next()) {
			ids.add(queryForRowSet.getLong(1));
		}

		return ids;
	}

	@Override
	public PrintNode loadRow(Long nodeid) throws SaodException {
		String query = sqlQueries.getQuery("select_row_node_id.sql", true);
		final SqlRowSet queryForRowSet = this.jdbcTemplate.queryForRowSet(query, nodeid);

		while (queryForRowSet.next()) {
			PrintNode node = new PrintNode(nodeid);
			node.setParent(queryForRowSet.getLong(2));
			node.setLocalSize(queryForRowSet.getLong(3));
			node.setFullSize(queryForRowSet.getLong(4));
			return node;
		}

		return null;
	}
}
