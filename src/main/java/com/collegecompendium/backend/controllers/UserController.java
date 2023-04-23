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

import com.collegecompendium.backend.configurations.UserProvider;
import com.collegecompendium.backend.models.Location;
import com.collegecompendium.backend.models.Student;
import com.collegecompendium.backend.models.User;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {
	@Autowired
	private UserProvider userProvider;

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
    	return userProvider.tokenToAuth0Data(token);
    }
    
    @GetMapping("/user")
    public User getUser(
    		@AuthenticationPrincipal Jwt token,
    		HttpServletResponse response
    		) {
    	
    	User user = userProvider.getUserForToken(token);
    	if (user != null) return user;
    	
    	Map<String, String> auth0Data = userProvider.tokenToAuth0Data(token);
    	user = Student.builder()
    			.email(auth0Data.get("email"))
    			.firstName(auth0Data.get("given_name"))
    			.lastName(auth0Data.get("family_name"))
    			.username(auth0Data.get("nickname"))
    			.id(null)
    			.location(new Location(0.0, 0.0))
    			.location(null)
    			.build();
    	
    	response.setStatus(404);
    	return user;
    }
}
