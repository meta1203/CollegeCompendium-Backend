package com.collegecompendium.backend.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests()
			.requestMatchers("/student", "/student/**")
				.hasAuthority("SCOPE_student")
			.requestMatchers("/college", "/college/**")
				.hasAuthority("SCOPE_college")
			.anyRequest()
				.authenticated();
		
		http.oauth2Client();
		
		return http.build();
	}
}
