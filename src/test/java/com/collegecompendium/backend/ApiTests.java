package com.collegecompendium.backend;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.collegecompendium.backend.configurations.UserProvider;

import lombok.extern.log4j.Log4j2;

@SpringBootTest
@ActiveProfiles("dev")
@Log4j2
public class ApiTests {
	@Autowired
	private UserProvider auth0Provider;
	
	@Value("${AUTH0_CLIENT:}")
	private String client;
	@Value("${AUTH0_SECRET:}")
	private String secret;
	
	@Test
	public void testAuth0TokenFetch() {
		log.info(client +  " | " + secret);
		
		// skip test if the auth0 environment variables aren't set 
		Assumptions.assumeFalse(secret == null || secret.isEmpty());
		Assumptions.assumeFalse(client == null || client.isEmpty());
		
		String mgmtToken = auth0Provider.getManagementToken();
		assertNotNull(mgmtToken);
		assertFalse(mgmtToken.isEmpty());
	}
}
