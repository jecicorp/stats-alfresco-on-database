package fr.jeci.alfresco.saod;

import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import fr.jeci.alfresco.saod.sql.SqlQueries;

@Configuration
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class })
@ComponentScan
@EnableGlobalMethodSecurity(securedEnabled = true)
/**
 * Class Main of the application
 */
public class SaodApplication extends SpringBootServletInitializer {
	@Bean
	MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize("51200KB"); // 50MB
		factory.setMaxRequestSize("256000KB");
		return factory.createMultipartConfig();
	}

	/**
	 * Main
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new SpringApplicationBuilder(SaodApplication.class).run(args);
	}

	/**
	 * Configure the application
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SaodApplication.class);
	}

	@Bean
	@ConfigurationProperties(prefix = "alfresco.datasource")
	/**
	 * Create a DataSource from an Alfresco DataSource
	 * @return the DataSource created
	 */
	public DataSource alfrescoDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	@ConfigurationProperties(prefix = "local.datasource")
	@Primary
	/**
	 * Create a DataSource from a local DataSource
	 * @return the DataSource created
	 */
	public DataSource localDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Value("${sql.local.base_path}")
	private String sqlLocalBasePath = "sql/hsqldb";

	@Bean
	/**
	 * Create queries and give it to the local base
	 * @return a query or queries
	 */
	public SqlQueries localSqlQueries() {
		SqlQueries sqlQueries = new SqlQueries();
		sqlQueries.setSqlBasePath(sqlLocalBasePath);
		return sqlQueries;
	}

	@Value("${sql.alfresco.base_path}")
	private String sqlAlfrescoBAsePath = "sql/hsqldb";

	@Bean
	/**
	 * Create queries and give it to Alfresco base
	 * @return a query or queries
	 */
	public SqlQueries alfrescoSqlQueries() {
		SqlQueries sqlQueries = new SqlQueries();
		sqlQueries.setSqlBasePath(sqlAlfrescoBAsePath);
		return sqlQueries;
	}

	@Bean
	/**
	 * Create a new application Security
	 * @return the application created
	 */
	public ApplicationSecurity applicationSecurity() {
		return new ApplicationSecurity();
	}

	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Configuration
	protected static class AuthenticationSecurity extends GlobalAuthenticationConfigurerAdapter {

		@Autowired
		@Qualifier("localDataSource")
		private DataSource dataSource;

		@Override
		public void init(AuthenticationManagerBuilder auth) throws Exception {
			auth.jdbcAuthentication().dataSource(dataSource);
		}

	}

	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

		// @formatter:off
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests()
					.antMatchers("/login").permitAll()
					.anyRequest().fullyAuthenticated()
				.and()
					.formLogin().loginPage("/login")
					.failureUrl("/login?error")
				.and()
					.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.and()
					.exceptionHandling().accessDeniedPage("/access?error");
		}
		// @formatter:on

	}

}
