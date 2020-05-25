package fr.jeci.alfresco.saod.sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import fr.jeci.alfresco.saod.SaodException;
import fr.jeci.alfresco.saod.pojo.NodeStat;

@Component
public class AlfrescoDaoImpl implements AlfrescoDao {
	static final Logger LOG = LoggerFactory.getLogger(AlfrescoDaoImpl.class);

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(@Qualifier("alfrescoDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.jdbcTemplate.setFetchSize(FETCH_SIZE);
	}

	@Autowired
	@Qualifier("alfrescoSqlQueries")
	private SqlQueries sqlQueries;

	@Override
	public void ping() throws SaodException {
		LOG.info("Ping Alfresco Database");
		String query = sqlQueries.getQuery("select_ping.sql");
		this.jdbcTemplate.execute(query);
	}

	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, readOnly = true)
	public Map<Long, Long> selectDirLocalSize() throws SaodException {
		String query = sqlQueries.getQuery("select_dir_local_size.sql");
		final SqlRowSet queryForRowSet = this.jdbcTemplate.queryForRowSet(query);

		final Map<Long, Long> libelle = new HashMap<>();
		while (queryForRowSet.next()) {
			libelle.put(queryForRowSet.getLong(1), queryForRowSet.getLong(2));
		}

		return libelle;
	}

	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, readOnly = true)
	public Map<Long, NodeStat> selectNodeStat() throws SaodException {
		String query = sqlQueries.getQuery("select_node_stat.sql");
		final SqlRowSet queryForRowSet = this.jdbcTemplate.queryForRowSet(query);

		final Map<Long, NodeStat> libelle = new HashMap<>();
		while (queryForRowSet.next()) {
			NodeStat stat = new NodeStat(queryForRowSet.getLong(2), queryForRowSet.getInt(3));// 3 : nb elements
			libelle.put(queryForRowSet.getLong(1), stat);
		}

		return libelle;
	}

	/*
	 * ORA-01795: maximum number of expressions in a list is 1000
	 */
	private final static int MAX_NUM_EXP_LIST = 1000;

	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, readOnly = true)
	public Map<Long, Long> selectParentNodeId(List<Long> child_id) throws SaodException {
		NamedParameterJdbcTemplate jdbcNamesTpl = new NamedParameterJdbcTemplate(this.jdbcTemplate);

		final String query = sqlQueries.getQuery("select_parent_node_id.sql");
		final Map<Long, Long> libelle = new HashMap<>();

		for (int i = 0; i < child_id.size(); i += MAX_NUM_EXP_LIST) {
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("ids", child_id.subList(i, Math.min(i + MAX_NUM_EXP_LIST, child_id.size())));

			final SqlRowSet queryForRowSet = jdbcNamesTpl.queryForRowSet(query, parameters);
			while (queryForRowSet.next()) {
				libelle.put(queryForRowSet.getLong(1), queryForRowSet.getLong(2));
			}

		}

		return libelle;
	}

	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, readOnly = true)
	public String selectNodeLabel(Long id) throws SaodException {
		String query = sqlQueries.getQuery("select_node_label.sql");
		try {
			return this.jdbcTemplate.queryForObject(query, new Object[] { id }, String.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, readOnly = true)
	public String selectNodeRef(Long id) throws SaodException {
		String query = sqlQueries.getQuery("select_node_noderef.sql");
		try {
			// protocol, identifier, uuid
			Map<String, Object> rMap = this.jdbcTemplate.queryForMap(query, id);
			return String.format("%s://%s/%s", rMap.get("protocol"), rMap.get("identifier"), rMap.get("uuid"));
		} catch (EmptyResultDataAccessException e) {
			return "NNF";
		}
	}
}
