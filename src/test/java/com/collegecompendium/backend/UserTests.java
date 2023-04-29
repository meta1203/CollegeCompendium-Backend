package com.collegecompendium.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.collegecompendium.backend.configurations.UserProvider;
import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.CollegeAdmin;
import com.collegecompendium.backend.models.Location;
import com.collegecompendium.backend.models.Student;
import com.collegecompendium.backend.models.User;
import com.collegecompendium.backend.repositories.CollegeAdminRepository;
import com.collegecompendium.backend.repositories.CollegeRepository;
import com.collegecompendium.backend.repositories.StudentRepository;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("dev")
public class UserTests {
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private CollegeAdminRepository collegeAdminRepository;
	@Autowired
	private CollegeRepository collegeRepository;
	@Autowired
	private Jwt injectedJwt;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private UserProvider userProvider;
	 
	@BeforeEach
	public void setup() {

	}
	//Need to add find by CollegeId test when we create College objects
	@Test
	@Order(1)
	void testStudentRepo() {
//		User user = userProvider.getUserForToken(injectedJwt);
//    	if (user != null) userProvider.deleteUser(user);
    	
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
	@Order(2)
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

	@Test
	@Order(3)
	public void testAddFavCollege() {
		Student testStudent = Student.builder()
				.email("PrestonAndHamdy4Eva@languageDesigners.com")
				.firstName("Chad")
				.lastName("Sexton")
				.location(new Location("Darlington Ct., Montgomery Village, MD 20886",
						"14.063226",
						"-10.905866"))
				.username("EgyptIz4Luvers")
				.auth0Id(injectedJwt.getSubject())
				.build();
		
		testStudent = studentRepository.save(testStudent);
		College testFavCollege = null;
		testFavCollege = College.builder()
				.name("Favorite This School")
				.inStateCost(30000)
				.outStateCost(40000)
				.location(new Location(
						"Somewhere Over the Rainbow",
						"34.06609123582969",
						"-106.9056496898088"
						))
				.description("The second test college")
				.popularity(80085)
				.id("CollegeToBeFavorited")
				.url("https://www.plz.edu/")
				.build();
		testFavCollege = collegeRepository.save(testFavCollege);
	
	    RequestEntity<Void> request = RequestEntity
	            .put(URI.create("http://localhost:8080/student/favorite/college?id=" + testFavCollege.getId()))
	            .header("Authorization", "Bearer " + injectedJwt.getTokenValue())
	            .build();

	    ResponseEntity<College> response = restTemplate.exchange(request, College.class);
	    assertEquals(HttpStatus.OK, response.getStatusCode());

	    // Retrieve the student again from the database
	    Student student = studentRepository.findById(testStudent.getId()).orElseThrow();

	    // Verify that the student's list of favorite colleges contains the added college
	    assertTrue(student.getFavoriteColleges().contains(testFavCollege));
	    
	    studentRepository.delete(testStudent);
	    collegeRepository.delete(testFavCollege);
	}
}
