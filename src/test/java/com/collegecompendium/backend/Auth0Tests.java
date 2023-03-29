package com.collegecompendium.backend;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.collegecompendium.backend.configurations.Auth0Provider;

import lombok.extern.log4j.Log4j2;

@SpringBootTest
@ActiveProfiles("dev")
@Log4j2
public class Auth0Tests {
	@Autowired
	private Auth0Provider auth0Provider;
	
	@Value("${AUTH0_CLIENT:}")
	private String client;
	@Value("${AUTH0_SECRET:}")
	private String secret;
	
	@Test
	public void testTokenFetch() {
		log.info(client +  " | " + secret);
		
		// skip test if the auth0 environment variables aren't set 
		Assumptions.assumeFalse(secret == null || secret.isEmpty());
		Assumptions.assumeFalse(client == null || client.isEmpty());
		
		log.info("got the following token: " + auth0Provider.getManagementToken());
	}
}
