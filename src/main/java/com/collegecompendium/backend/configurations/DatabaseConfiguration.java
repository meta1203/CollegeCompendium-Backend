package com.collegecompendium.backend.configurations;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

// Spring annotation - almost identical to @Component, just
// alerts the user that this class is specifically for 
// configuration and not just generic bean declaration

/**
 * This class is used to configure the database connection.
 */
@Configuration
public class DatabaseConfiguration {
	/*
	 * Unless you're 100% SURE that code won't hit production, you should always use a connection pooler.
	 * This registers a DataSource bean using the common HikariCP connection pool.
	 * You can change it to a connection pooler of your choice if need be, just make sure it returns something
	 * that implements javax.sql.DataSource.
	 */
	private HikariConfig config = new HikariConfig();
	
	// fetch properties for connecting to the db
	@Value("${DB_URL:}")
	private String url;
	@Value("${DB_DATABASE:}")
	private String database;
	@Value("${DB_USERNAME:}")
	private String username;
	@Value("${DB_PASSWORD:}")
	private String password;

	// Spring annotation - defines a "Spring Bean"; promotes an instantiated object
	// to a singleton that can be injected anywhere Spring touches by using the 
	// @Autowired annotation
	@Bean
	// Spring annotation - limits the annotated entity to only be active when the
	// specified Spring profile is active
	// https://www.baeldung.com/spring-profiles
	@Profile("dev")
	/**
	 * This method is used to configure the database connection for dev environment.
	 * @return DataSource object
	 */
	DataSource inMemoryDataSource() {
		// set the data source's JDBC URL.
		// I'm using H2's in-memory DB for demonstration purposes 
		config.setJdbcUrl("jdbc:h2:mem:testdb");

		// set the username and password for the DB. set this how you want, just don't hard-code it in plain-text
		// H2's in-memory DB doesn't require a username or password, but they're commented here for the sake of completeness
		//config.setUsername(System.getenv("DB_USERNAME"));
		//config.setPassword(System.getenv("DB_PASSWORD"));

		// enforce transactionality
		config.setAutoCommit(false);

		// some basic connection pooling stuff. tweak as needed
		config.setMinimumIdle(5);
		config.setMaximumPoolSize(15);

		// basic test query to see if the connection is alive. SELECT 1 should work for most stuff, but you might need to change it.
		config.setConnectionTestQuery("SELECT 1");

		return new HikariDataSource(config);
	}

	@Bean
	@Profile("prod")
	/**
	 * This method is used to configure the database connection for production environment.
	 * @return DataSource object
	 */
	DataSource mariaDataSource() {
		// get db connection string from environment variables
		config.setJdbcUrl("jdbc:mariadb://" + url + ":3306/" + database);

		// get DB username from environment variables
		config.setUsername(username);
		// get DB password from environment variables
		config.setPassword(password);

		// enforce transactionality
		config.setAutoCommit(false);

		// some basic connection pooling stuff. tweak as needed
		config.setMinimumIdle(5);
		config.setMaximumPoolSize(15);

		// basic test query to see if the connection is alive. SELECT 1 should work for most stuff, but you might need to change it.
		config.setConnectionTestQuery("SELECT 1");

		return new HikariDataSource(config);
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplateBuilder()
				.errorHandler(new ResponseErrorHandler() {
					@Override
					public boolean hasError(ClientHttpResponse response) throws IOException {
						// This we always want to accept a response, 
						// regardless of error code
						return false;
					}

					@Override
					public void handleError(ClientHttpResponse response) throws IOException {
						// this will never get called due to hasError always
						// returning false, so do nothing
					}

					@Override
					public String toString() {
						return "Hunter's fancy custom REST error handler :)";
					}
				})
				.build();

	}
}
