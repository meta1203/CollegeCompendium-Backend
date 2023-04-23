package com.collegecompendium.backend.configurations;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import com.collegecompendium.backend.models.CollegeAdmin;
import com.collegecompendium.backend.models.Student;
import com.collegecompendium.backend.models.User;
import com.collegecompendium.backend.repositories.CollegeAdminRepository;
import com.collegecompendium.backend.repositories.StudentRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UserProvider {
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private CollegeAdminRepository collegeAdminRepository;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private Jwt injectedJwt;
	
	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	private String url;
	@Value("${AUTH0_CLIENT:}")
	private String client;
	@Value("${AUTH0_SECRET:}")
	private String secret;

	// gets everything auth0 knows about the given JWT token
	// returns a HashMap because there's no documentation I can find
	// that provides a fixed model
	public Map<String,String> tokenToAuth0Data(Jwt token) {
		// use the injected token if we're performing tests
		if (token.getTokenValue().equals(injectedJwt.getTokenValue())) {
			return Map.of(
					"email", "john.smith@example.com",
					"given_name", "John",
					"family_name", "Smith",
					"nickname", "jsmith12"
					);
		}

		RequestEntity<Void> request = RequestEntity
				.get(URI.create(url + "userinfo"))
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token.getTokenValue()).build();

		ResponseEntity<HashMap<String, String>> response = restTemplate.exchange(
				request,
				new ParameterizedTypeReference<HashMap<String, String>>(){}
				);

		if (response.getStatusCode().is2xxSuccessful()) return response.getBody();
		return null;
	}
	
	public void addPermissionToUser(User user, String permission) {
		// if this is the test user, don't do anything
		if (user.getAuth0Id().equals(injectedJwt.getSubject()))
			return;
		
		RequestEntity<String> req = RequestEntity.post(URI.create(url + "api/v2/users/" + UriUtils.encode(user.getAuth0Id(), "UTF-8") + "/permissions"))
				.contentType(MediaType.APPLICATION_JSON)
				.header("authorization", "Bearer " + this.getManagementToken())
				.header("cache-control", "no-cache")
				.body("{\"permissions\":[{\"resource_server_identifier\":\"https://tcfc.us.to/cc_api\",\"permission_name\":\""+permission+"\"}]}");
		
		ResponseEntity<Void> resp = restTemplate.exchange(req, Void.class);
		if (! resp.getStatusCode().is2xxSuccessful()) {
			log.error("error getting " + req.getUrl());
			throw new RuntimeException("Bad response code " + resp.getStatusCode().value() + " when adding Auth0 permission.");
		}
	}

	private String token = null;
	private Instant obtained = null;
	private int validFor = 0;

	public String getManagementToken() {
		if (token == null)
			return refreshToken();
		// check validity, minus ten seconds to give us a small buffer window to
		// prevent potential rejections when time is running out
		if (Instant.now().isAfter(obtained.plus(validFor - 10, ChronoUnit.SECONDS)))
			return refreshToken();
		return token;
	}

	private String refreshToken() {
		RequestEntity<String> req = RequestEntity
				.post(URI.create(url + "oauth/token"))
				.contentType(MediaType.APPLICATION_JSON)
				.body("{\"client_id\":\""+client+"\",\"client_secret\":\""+ secret + "\",\"audience\":\"https://dev-yrjc5x2ila2084mu.us.auth0.com/api/v2/\",\"grant_type\":\"client_credentials\"}");
		
		// log.info("requesting " + req.getUrl());

		ResponseEntity<Map<String,String>> resp = restTemplate
				.exchange(req, new ParameterizedTypeReference<Map<String,String>>(){});
		if (!resp.getStatusCode().is2xxSuccessful())
			throw new RuntimeException("Bad response code " + resp.getStatusCode().value() + " when fetching Auth0 machine token.");

		this.obtained = Instant.now();
		Map<String,String> body = resp.getBody();
		this.token = body.get("access_token");
		this.validFor = Integer.parseInt(body.get("expires_in"));
		// log.info("Auth0 token permissions: " + body.get("scope"));

		return this.token;
	}
	
	public User getUserForToken(Jwt token) {
		Student student = studentRepository.findDistinctByAuth0Id(token.getSubject());
		if (student != null) return student;
		CollegeAdmin collegeAdmin = collegeAdminRepository.findDistinctByAuth0Id(token.getSubject());
		return collegeAdmin;
	}
	
	public boolean deleteUser(User user) {
		if (user instanceof CollegeAdmin) {
			collegeAdminRepository.delete((CollegeAdmin)user);
			return true;
		} else if (user instanceof Student) {
			studentRepository.delete((Student)user);
			return true;
		}
		return false;
	}
}
