package com.collegecompendium.backend.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.collegecompendium.backend.configurations.Auth0Provider;
import com.collegecompendium.backend.models.CollegeAdmin;
import com.collegecompendium.backend.models.Student;
import com.collegecompendium.backend.models.User;
import com.collegecompendium.backend.repositories.CollegeAdminRepository;
import com.collegecompendium.backend.repositories.StudentRepository;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private CollegeAdminRepository collegeAdminRepository;
	@Autowired
	private Auth0Provider auth0Provider;

    @PostMapping("/test/user")
    public User addUser(@RequestBody User user) {
    	// TODO: validate input
        // user = userRepository.save(user);
        return user;
    }
    
    // used to test endpoint security
    @GetMapping("/test")
    public Object pingAuth(@AuthenticationPrincipal Jwt token) {
    	//return token;
    	return auth0Provider.tokenToAuth0Data(token);
    }
    
    @GetMapping("/user")
    public User getUser(
    		@AuthenticationPrincipal Jwt token,
    		HttpServletResponse response
    		) {
    	
    	Student student = studentRepository.findDistinctByAuth0Id(token.getSubject());
    	if (student != null) {
    		return student;
    	}
    	
    	CollegeAdmin collegeAdmin = collegeAdminRepository.findDistinctByAuth0Id(token.getSubject());
    	if (collegeAdmin != null) {
    		return collegeAdmin;
    	}
    	
    	Map<String, String> auth0Data = auth0Provider.tokenToAuth0Data(token);
    	student = Student.builder()
    			.email(auth0Data.get("email"))
    			.firstName(auth0Data.get("given_name"))
    			.lastName(auth0Data.get("family_name"))
    			.username(auth0Data.get("nickname"))
    			.id(null)
    			.build();
    	
    	response.setStatus(404);
    	return student;
    }
}
