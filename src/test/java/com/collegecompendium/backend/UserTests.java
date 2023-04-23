package com.collegecompendium.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import com.collegecompendium.backend.configurations.UserProvider;
import com.collegecompendium.backend.models.CollegeAdmin;
import com.collegecompendium.backend.models.Location;
import com.collegecompendium.backend.models.Student;
import com.collegecompendium.backend.models.User;
import com.collegecompendium.backend.repositories.CollegeAdminRepository;
import com.collegecompendium.backend.repositories.StudentRepository;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("dev")
public class UserTests {
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private CollegeAdminRepository collegeAdminRepository;
	@Autowired
	private Jwt injectedJwt;
	@Autowired
	private UserProvider userProvider;
	
	//Need to add find by CollegeId test when we create College objects
	@Test
	void testStudentRepo() {
		User user = userProvider.getUserForToken(injectedJwt);
    	if (user != null) userProvider.deleteUser(user);
    	
		final String EMAIL_ADDRESS = "hancock.hunter@gmail.com";
		// create
		Student student = Student.builder()
				.email(EMAIL_ADDRESS)
				.firstName("Hunter")
				.lastName("Hancock")
				.middleInitial("A")
				.location(new Location("Neel Dr, Socorro, NM 87801", "34.063226", "-106.905866"))
				.username("meta1203")
				.auth0Id(injectedJwt.getSubject())
				.build();
		
		student = studentRepository.save(student);
		assertNotNull(student.getId());
		assertEquals(student.getEmail(), EMAIL_ADDRESS);
		
		String id = student.getId();
		
		System.out.println(student);
		
		// read
		Student fetchedStudent = studentRepository.findDistinctByUsername("meta1203");
		assertEquals(student.getId(), fetchedStudent.getId());
		assertEquals(student.getEmail(), fetchedStudent.getEmail());
		
		// update
		// test auto-update
		student.setUsername("meta42069");
		studentRepository.save(student);
		
		fetchedStudent = studentRepository.findById(id).get();
		assertEquals(student.getUsername(), fetchedStudent.getUsername());
		
		// delete
		studentRepository.delete(student);
		assertTrue(studentRepository.findById(id).isEmpty());
	}
	
	@Test
	void testCollegeAdminRepo() {
		User user = userProvider.getUserForToken(injectedJwt);
    	if (user != null) userProvider.deleteUser(user);
    	
		final String EMAIL_ADDRESS = "Vote.Hamdy@gmail.com";
		// C
		CollegeAdmin collegeAdmin = CollegeAdmin.builder()
				.email(EMAIL_ADDRESS)
				.firstName("Erik")
				.lastName("Noll")
				.middleInitial("E")
				.username("mountainDewYoloSwag420")
				.auth0Id(injectedJwt.getSubject())
				.build();
		
		collegeAdmin = collegeAdminRepository.save(collegeAdmin);
		assertNotNull(collegeAdmin.getId());
		assertEquals(collegeAdmin.getEmail(), EMAIL_ADDRESS);
		
		String collegeAdminId = collegeAdmin.getId();
		
		System.out.println(collegeAdmin);
	
		// R
		CollegeAdmin fetchedCollegeAdmin = collegeAdminRepository.findDistinctByUsername("mountainDewYoloSwag420");
		assertEquals(collegeAdmin.getId(), fetchedCollegeAdmin.getId());
		assertEquals(collegeAdmin.getEmail(), fetchedCollegeAdmin.getEmail());
		
		// update
		// test auto-update
		collegeAdmin.setUsername("itmeUrBro");
		collegeAdminRepository.save(collegeAdmin);
				
		fetchedCollegeAdmin = collegeAdminRepository.findById(collegeAdminId).get();
		assertEquals(collegeAdmin.getUsername(), fetchedCollegeAdmin.getUsername());
				
		// delete
		collegeAdminRepository.delete(collegeAdmin);
		assertTrue(collegeAdminRepository.findById(collegeAdminId).isEmpty());
	}
}
