package com.collegecompendium.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.collegecompendium.backend.models.Student;
import com.collegecompendium.backend.repositories.StudentRepository;

@SpringBootTest
@ActiveProfiles("dev")
public class UserTests {
	@Autowired
	private StudentRepository studentRepository;
	
	@Test
	void testStudentRepo() {
		final String EMAIL_ADDRESS = "hancock.hunter@gmail.com";
		// create
		Student student = Student.builder()
				.email(EMAIL_ADDRESS)
				.firstName("Hunter")
				.lastName("Hancock")
				.middleInitial("A")
				.location("Socorro, New Mexico")
				.username("meta1203")
				.auth0Id("an auth0 id")
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
}
