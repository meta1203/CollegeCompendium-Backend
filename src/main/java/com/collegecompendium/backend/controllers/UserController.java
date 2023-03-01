package com.collegecompendium.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.collegecompendium.backend.models.User;
import com.collegecompendium.backend.repositories.UserRepository;

@RestController
public class UserController {
	@Autowired
    private UserRepository userRepository;

    @PostMapping("/user")
    public User addUser(@RequestBody User user) {
    	// TODO: validate input
        user = userRepository.save(user);
        return user;
    }
}
