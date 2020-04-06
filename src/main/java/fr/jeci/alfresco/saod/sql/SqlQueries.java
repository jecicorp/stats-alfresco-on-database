package fr.jeci.alfresco.saod.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import fr.jeci.alfresco.saod.SaodException;

/**
 * Chargement des fichiers SQL
 * 
 * @author jlesage
 */
public class SqlQueries {
	static final Logger LOG = LoggerFactory.getLogger(SqlQueries.class);

	public final static String COMMENT_PREFIX = "--";
	public final static String SEPARATOR = ";";

	private HashMap<String, String> cacheQeries;
	private PathMatchingResourcePatternResolver resolver;

	private String sqlBasePath = "sql/hsqldb";

	/**
	 * constructor of SQL Queries
	 */
	public SqlQueries() {
		this.cacheQeries = new HashMap<>();
		this.resolver = new PathMatchingResourcePatternResolver();
	}

	/**
	 * Affect a path to the SQL Base
	 * @param sqlBasePath
	 */
	public void setSqlBasePath(String sqlBasePath) {
		this.sqlBasePath = sqlBasePath;
	}

	/**
	 * Search a query by id
	 * @param id
	 * @return
	 * @throws SaodException
	 */
	public String getQuery(String id) throws SaodException {
		String query = this.cacheQeries.get(id);

		if (query == null) {
			query = loadQuery(id);
		}

		return query;
	}

	/**
	 * Load a query thanks to the id
	 * @param id
	 * @return
	 * @throws SaodException
	 */
	private String loadQuery(final String id) throws SaodException {
		String ressourcePath = this.sqlBasePath + "/" + id;
		LOG.info("Loading sql file : {}", ressourcePath);
		try {
			Resource[] ressources = resolver.getResources("classpath*:" + ressourcePath);
			InputStream resourceAsStream = ressources[0].getInputStream();
			if (resourceAsStream == null) {
				File file = new File(ressourcePath);
				if (!file.exists()) {
					throw new SaodException("Fichier sql '" + ressourcePath + "' introuvable");
				}
				resourceAsStream = new FileInputStream(file);
			}

			try (InputStreamReader reader = new InputStreamReader(resourceAsStream)) {
				loadSQL(id, reader);
				return this.cacheQeries.get(id);
			}
		} catch (IOException e) {
			throw new SaodException("Erreur lors du chargement du fichier sql : " + ressourcePath, e);
		}
	}

	/**
	 * Load all query with corresponding id in a table
	 * @param id
	 * @param reader
	 * @throws IOException
	 */
	private void loadSQL(String id, InputStreamReader reader) throws IOException {
		LineNumberReader fileReader = new LineNumberReader(new BufferedReader(reader));
		String query = ScriptUtils.readScript(fileReader, COMMENT_PREFIX, SEPARATOR);
		cacheQeries.put(id, query);
	}

}
