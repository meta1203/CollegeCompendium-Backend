package com.collegecompendium.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.collegecompendium.backend.models.User;
import com.collegecompendium.backend.repositories.UserRepository;

import lombok.Builder;

@SpringBootTest
@ActiveProfiles("dev")
public class UserTests {
	@Autowired
	private UserRepository userRepository;
	
	@Test
	void testUserRepo() {
		final String EMAIL_ADDRESS = "hancock.hunter@gmail.com";
		// create
		User user = User.builder()
				.email(EMAIL_ADDRESS)
				.firstName("Hunter")
				.lastName("Hancock")
				.middleInitial("A")
				.location("Socorro, New Mexico")
				.username("meta1203")
				.build();
		
		user = userRepository.save(user);
		assertNotNull(user.getId());
		assertEquals(user.getEmail(), EMAIL_ADDRESS);
		
		String id = user.getId();
		
		System.out.println(user);
		
		// read
		User fetchedUser = userRepository.findDistinctByUsername("meta1203");
		assertEquals(user.getId(), fetchedUser.getId());
		assertEquals(user.getEmail(), fetchedUser.getEmail());
		
		// update
		// test auto-update
		user.setUsername("meta42069");
		userRepository.save(user);
		
		fetchedUser = userRepository.findById(id).get();
		assertEquals(user.getUsername(), fetchedUser.getUsername());
		
		// delete
		userRepository.delete(user);
		assertTrue(userRepository.findById(id).isEmpty());
	}
}
