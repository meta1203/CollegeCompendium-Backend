package com.collegecompendium.backend.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// enable JWT token handling
		http.oauth2ResourceServer()
			.jwt()
				// use the permissions claim on the provided token for auto conversion
				.jwtAuthenticationConverter(this.jwtAuthenticationConverter());
		
		// enforce permissions 
		http.authorizeHttpRequests()
			.requestMatchers("/student", "/student/**")
				.hasAuthority("PERM_student")
			.requestMatchers("/college", "/college/**")
				.hasAuthority("PERM_college")
			.anyRequest()
				.authenticated();
		
		http.cors();
		
		// finalize the security configuration
		return http.build();
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
