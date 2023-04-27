package com.collegecompendium.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.collegecompendium.backend.models.CollegeAdmin;
import com.collegecompendium.backend.models.Location;
import com.collegecompendium.backend.models.Student;
import com.collegecompendium.backend.repositories.CollegeAdminRepository;
import com.collegecompendium.backend.repositories.CollegeRepository;
import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.configurations.LocationProvider;
import com.collegecompendium.backend.configurations.UserProvider;
import com.collegecompendium.backend.models.Location;
import com.collegecompendium.backend.models.Student;
import com.collegecompendium.backend.models.User;

import lombok.extern.log4j.Log4j2;

// Spring annotation - flags this as a test suite
// DEFINED_PORT is used to get the web server up and running
// for any integration testing we want to do
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
// Spring annotation - uses the given profile for tests
@ActiveProfiles("dev")
// run in order specified by @Order
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Log4j2
class BackendApplicationTests {
	@Autowired
	private Jwt injectedJwt;
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private LocationProvider locationProvider;
	@Autowired
	private UserProvider userProvider;
	
	@Test
	@Order(0)
	void contextLoads() {}
	
	@Test
	@Order(1)
	// test New User Experience endpoints
	void testNewUser() {
		// clear existing user, if it exists
		User u = userProvider.getUserForToken(injectedJwt);
		if (u != null)
			userProvider.deleteUser(u);
		
		// validate we can get an incomplete user object
		RequestEntity<Void> req1 = RequestEntity
				.get(URI.create("http://localhost:8080/user"))
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + injectedJwt.getTokenValue())
				.build();
		
		ResponseEntity<Student> resp = restTemplate.exchange(req1, Student.class);
		// do expected value first, followed by tested value
		// this is because should the assertion fail, the message
		// is `expected: <first> but was: <second>
		assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
		Student student = resp.getBody();
		assertNotNull(student);
		
		// validate we can submit the new Student
		student.setActScore(31);
		student.setMiddleInitial("A");
		student.setHighschool("Example High School");
		student.setLocation(new Location("Neel Dr, Socorro, NM 87801", "34.063226", "-106.905866"));
		
		RequestEntity<Student> req2 = RequestEntity
				.post(URI.create("http://localhost:8080/student"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + injectedJwt.getTokenValue())
				.body(student);
		
		resp = restTemplate.exchange(req2, Student.class);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		student = resp.getBody();
		assertTrue(student.getId() != null && !student.getId().isEmpty());
		assertTrue(student.getAuth0Id() != null && !student.getAuth0Id().isEmpty());
		
		// validate that we cannot resubmit the new student
		resp = restTemplate.exchange(req2, Student.class);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		assertNull(resp.getBody());
	}
	
	@Test
	@Order(2)
	void testLocation() throws InterruptedException {
		Location abq = locationProvider.findLocation("Albuquerque, New Mexico");
		Thread.sleep(1100);
		Location denv = locationProvider.findLocation("Denver, Colorado");
		log.warn(abq.getLatitude() + " | " + abq.getLongitude());
		assertEquals("35.212871", abq.getLatitudeFixedPrecision());
		assertEquals("-104.984862", denv.getLongitudeFixedPrecision());
		log.warn("distance is " + abq.distanceFrom(denv));
		assertEquals(4.845131996719168d, abq.distanceFrom(denv));
		assertEquals(0, abq.distanceFrom(abq));
	}
	
	
}
