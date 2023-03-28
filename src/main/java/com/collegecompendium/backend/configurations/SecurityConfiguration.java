package com.collegecompendium.backend.configurations;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
public class SecurityConfiguration {
	@Profile("prod")
	@Bean
	SecurityFilterChain productionSecurity(HttpSecurity http) throws Exception {
		// get default security settings
		http = basicSecurity(http);
		// finalize the security configuration
		return http.build();
	}

	@Profile("dev")
	@Bean
	SecurityFilterChain developmentSecurity(
			HttpSecurity http,
			JwtDecoder jwtDecoder,
			Jwt injectedToken
			) throws Exception {
		// get default security settings
		http = basicSecurity(http);
		// add custom jwt decoder
		// this will let us use our injectedToken to perform
		// automated testing
		http.oauth2ResourceServer().jwt().decoder(token -> {
			if (token.equals(injectedToken.getTokenValue()))
				return injectedToken;
			return jwtDecoder.decode(token);
		});
		// finalize the security configuration
		return http.build();
	}

    @Bean
    Jwt injectedJwt() {
        Jwt ret = new Jwt(
                "token1234",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                // claims
                Map.of(
                        "sub", "test|1234",
                        "permissions", List.of("student", "collegeAdmin")
                )
        );
        log.error("LOOK ==> injected token is " + ret.getTokenValue());
        return ret;
    }
	
	// gets everything auth0 knows about the given JWT token
	// returns a HashMap because there's no documentation I can find
	// that provides a fixed model 
	/* @Bean
	Function<Jwt, Map<String, String>> tokenToAuth0Data(
			RestTemplate rt,
			Jwt injectedJwt
			) {
		return (token) -> {
			if (token.getTokenValue().equals(injectedJwt.getTokenValue()))
				return Map.of(
						"email", "john.smith@example.com",
						"given_name", "John",
						"family_name", "Smith",
						"nickname", "jsmith12"
						);
			
			RequestEntity<Void> request = RequestEntity
					.get(URI.create("https://dev-yrjc5x2ila2084mu.us.auth0.com/userinfo"))
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + token.getTokenValue()).build();
			
			ResponseEntity<HashMap<String, String>> response = rt.exchange(
					request, 
					new ParameterizedTypeReference<HashMap<String, String>>() {}
					);
			
			if (response.getStatusCode().is2xxSuccessful()) return response.getBody();
			return null;
		};
	} */

	private HttpSecurity basicSecurity(HttpSecurity http) throws Exception {
		// enable JWT token handling
		http.oauth2ResourceServer()
			.jwt()
				// use the permissions claim on the provided token for auto conversion
				.jwtAuthenticationConverter(this.jwtAuthenticationConverter());

		// enforce permissions
		// changed /college to /collegeAdmin
		http.authorizeHttpRequests()
			.requestMatchers("/student", "/student/**")
				.hasAuthority("PERM_student")
			.requestMatchers("/collegeAdmin", "/collegeAdmin/**")
				.hasAuthority("PERM_collegeAdmin")
			.anyRequest()
				.authenticated();

		// enable CORS
		http.cors();
		
		return http;
	}

	private JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
		converter.setAuthoritiesClaimName("permissions");
		converter.setAuthorityPrefix("PERM_");

		JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
		jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
		return jwtConverter;
	}
}
