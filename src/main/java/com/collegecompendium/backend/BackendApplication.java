package com.collegecompendium.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
//load the local .env file to fetch necessary environment data
@PropertySource(value = "file:./.env", ignoreResourceNotFound = true)
/**
 * Main class for the backend application
 */
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
