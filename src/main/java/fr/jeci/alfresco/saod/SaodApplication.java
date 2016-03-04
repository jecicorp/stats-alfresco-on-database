package fr.jeci.alfresco.saod;

import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class })
@ComponentScan
public class SaodApplication extends SpringBootServletInitializer {
	@Bean
	MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize("51200KB"); // 50MB
		factory.setMaxRequestSize("256000KB");
		return factory.createMultipartConfig();
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SaodApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SaodApplication.class);
	}

	@Bean
	@ConfigurationProperties(prefix = "alfresco.datasource")
	public DataSource alfrescoDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	@Bean
	@ConfigurationProperties(prefix = "local.datasource")
	public DataSource localDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}
}
