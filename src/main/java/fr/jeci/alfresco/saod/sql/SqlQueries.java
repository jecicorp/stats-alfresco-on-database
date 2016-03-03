package fr.jeci.alfresco.saod.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import fr.jeci.alfresco.saod.SaodException;

/**
 * Chargement des fichiers SQL
 * 
 * @author jlesage
 */
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class SqlQueries {
	static final Logger LOG = LoggerFactory.getLogger(SqlQueries.class);

	public final static String COMMENT_PREFIX = "--";
	public final static String SEPARATOR = ";";

	private HashMap<String, String> cacheQeries;

	@Value("${sql.alfresco.query_path_folder}")
	private String sqlAlfrescoQueryPath = "sql/hsqldb";

	@Value("${sql.local.query_path_folder}")
	private String sqlLocalQueryPath = "sql/hsqldb";

	public SqlQueries() {
		this.cacheQeries = new HashMap<String, String>();
	}

	/**
	 * Si on a affaire à un vrai dossier, on charge les fichiers sql. Sinon on
	 * fera un chargement à la demande.
	 */
	@PostConstruct
	public void postConstruct() {

		File folderRessource = new File(this.sqlAlfrescoQueryPath);
		if (folderRessource != null && folderRessource.exists() && folderRessource.isDirectory()) {
			LOG.info("Using folder with SQL Queries : {}", folderRessource.getAbsolutePath());
			try {
				loadSQLDirectory(folderRessource);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		} else {
			LOG.error("folderRessource not found : " + folderRessource.getAbsolutePath());
		}
	}

	private void loadSQL(String id, File sqlFile) throws IOException {
		LOG.info("Load sql file {} with id {}", sqlFile, id);
		loadSQL(id, new FileReader(sqlFile));
	}

	private void loadSQL(String id, InputStreamReader reader) throws IOException {
		LineNumberReader fileReader = new LineNumberReader(new BufferedReader(reader));
		String query = ScriptUtils.readScript(fileReader, COMMENT_PREFIX, SEPARATOR);
		cacheQeries.put(id, query);
	}

	private void loadSQLDirectory(File dir) throws IOException {
		LOG.info("Load sql in directory {}", dir);

		if (!dir.isDirectory()) {
			LOG.error("{} is not a directory", dir);
			return;
		}

		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				continue;
			}

			if (!file.getName().endsWith("sql")) {
				continue;
			}

			loadSQL(file.getName(), file);
		}
	}

	public String getQuery(String id, boolean local) throws SaodException {
		String query = this.cacheQeries.get(id);

		if (query == null) {
			String ressourcePath = (local ? this.sqlLocalQueryPath : this.sqlAlfrescoQueryPath) + "/" + id;
			LOG.info("Loading sql file : {}", ressourcePath);
			try {
				InputStream resourceAsStream = SqlQueries.class.getResourceAsStream(ressourcePath);
				if (resourceAsStream == null) {
					resourceAsStream = new FileInputStream(new File(ressourcePath));
				}
//				if (resourceAsStream == null) {
//					throw new SaodException("Fichier sql '" + ressourcePath + "' introuvable");
//				}

				loadSQL(id, new InputStreamReader(resourceAsStream));
				query = this.cacheQeries.get(id);
			} catch (IOException e) {
				throw new SaodException("Erreur lors du chargement du fichier sql : " + ressourcePath, e);
			}

		}

		return query;
	}
}
