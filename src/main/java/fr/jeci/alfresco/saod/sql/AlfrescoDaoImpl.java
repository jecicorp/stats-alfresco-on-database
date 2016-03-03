package fr.jeci.alfresco.saod.sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
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

@Component
public class AlfrescoDaoImpl implements AlfrescoDao {
	static final Logger LOG = LoggerFactory.getLogger(AlfrescoDaoImpl.class);

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(@Qualifier("alfrescoDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Autowired
	private SqlQueries sqlQueries;

	private String selectDirLocalSize;
	private String selectParentNodeId;

	@PostConstruct
	public void afterPropertiesSet() throws Exception {
		selectDirLocalSize = sqlQueries.getQuery("select_dir_local_size.sql", false);
		selectParentNodeId = sqlQueries.getQuery("select_parent_node_id.sql", false);
	}

	@Override
	public Map<Long, Long> selectDirLocalSize() throws SaodException {
		final SqlRowSet queryForRowSet = this.jdbcTemplate.queryForRowSet(selectDirLocalSize);

		final Map<Long, Long> libelle = new HashMap<>();
		while (queryForRowSet.next()) {
			libelle.put(queryForRowSet.getLong(1), queryForRowSet.getLong(2));
		}

		return libelle;
	}

	@Override
	public Map<Long, Long> selectParentNodeId(List<Long> child_id) throws SaodException {
		NamedParameterJdbcTemplate jdbcNamesTpl = new NamedParameterJdbcTemplate(this.jdbcTemplate);
				
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("ids", child_id);

		final SqlRowSet queryForRowSet = jdbcNamesTpl.queryForRowSet(selectParentNodeId, parameters);

		final Map<Long, Long> libelle = new HashMap<>();
		while (queryForRowSet.next()) {
			libelle.put(queryForRowSet.getLong(1), queryForRowSet.getLong(2));
		}

		return libelle;
	}
}
