package com.collegecompendium.backend.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"http://localhost:3000", "https://cse326.meta1203.com/"})
@RestController
@RequestMapping(path = "/superAdmin")
public class SuperAdminController {
	@GetMapping("/test")
	public Jwt testSuperAdmin(@AuthenticationPrincipal Jwt token) {
		return token;
	}
}
