package com.collegecompendium.backend;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.collegecompendium.backend.models.*;
import com.collegecompendium.backend.repositories.*;

import lombok.extern.log4j.Log4j2;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Log4j2
public class SearchTests {
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private Jwt injectedJwt;
	@Autowired
	private CollegeRepository collegeRepository;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private CollegeAdminRepository collegeAdminRepository;

	College c1;
	
	@BeforeEach
	public void setup() {
		// TODO: replace with UserProvider
		Student me = Student.builder()
				.actScore(30)
				.college("")
				.auth0Id(injectedJwt.getSubject())
				.highschool("Example High")
				.email("test@example.com")
				.firstName("John")
				.middleInitial("Q")
				.lastName("Test")
				.location(new Location(
						"700 6th St, Socorro, NM 87801",
						"34.06536747225995", 
						"-106.88995469901924"
				))
				.satScore(0)
				.username("j.q.test")
				.build();
		
		me = studentRepository.save(me);
		
		// delete all colleges in db
		collegeRepository.findAll().forEach(c -> collegeRepository.delete(c));
		
		// prepopulate colleges
		c1 = College.builder()
				.name("New Mexico Tech")
				.inStateCost(30000)
				.outStateCost(40000)
				.location(new Location(
						"801 Leroy Pl, Socorro, NM 87801",
						"34.06609123582969",
						"-106.9056496898088"
				))
				.description("The first test college")
				// .phoneNumber("5758355133")
				.popularity(69)
				.url("https://www.nmt.edu/")
				.build();
		c1 = collegeRepository.save(c1);
		
		College c2 = College.builder()
				.name("Dirty Dave's Degrees and Dissertations")
				.inStateCost(100)
				.outStateCost(125)
				.location(new Location(
						"104 Neel St, Socorro, NM 87801",
						"34.059078845885246", 
						"-106.89836091130815"
				))
				.description("The second test college")
				// .phoneNumber("5059800370")
				.popularity(1)
				.url("http://cgnuonline-eniversity.edu")
				.build();
		c2 = collegeRepository.save(c2);
		
		College c3 = College.builder()
				.name("University of New Mexico")
				.inStateCost(900000)
				.outStateCost(100000000)
				.location(new Location(
						"1 University of New Mexico, Albuquerque NM 87131",
						"35.084397617922576", 
						"-106.61977049166329"
				))
				.description("The third test college")
				//.phoneNumber("5052778900")
				.popularity(420)
				.url("https://unm.edu")
				.build();
		c3 = collegeRepository.save(c3);
		
	}
	
	@Test
	@Order(1)
	public void locationSearch() {
		RequestEntity<Void> re = RequestEntity
				.get("http://localhost:8080/search/colleges/distance/{miles}", Integer.valueOf(10))
				.header("Authorization", "Bearer " + injectedJwt.getTokenValue())
				.build();
		ResponseEntity<List<College>> resp = restTemplate.exchange(re, 
				new ParameterizedTypeReference<List<College>>(){});
		assertTrue(resp.getStatusCode().is2xxSuccessful());
		log.warn(resp.getBody());
		
		List<College> colleges = resp.getBody();
		assertNotNull(colleges);
		assertFalse(colleges.isEmpty());
		
		colleges.forEach(c -> log.warn(c));
	}

	@Test
	@Order(2)
	public void findCollegeByIDTest(){
		String id = c1.getId(); // Get the ID of C1

		// Query the API for the college with the ID
		RequestEntity<Void> re = RequestEntity
				.get("http://localhost:8080/search/college/id/{id}", id)
				.header("Authorization", "Bearer " + injectedJwt.getTokenValue())
				.build();
		ResponseEntity<College> resp = restTemplate.exchange(re,
				new ParameterizedTypeReference<College>(){});
		assertTrue(resp.getStatusCode().is2xxSuccessful());
		log.warn(resp.getBody());

		College college = resp.getBody();
		assertNotNull(college);

		// Ensure the response the same as the college expected
		assertEquals(college.getId(), id);
		assertEquals(college.getName(), c1.getName());
	}

	@Test
	@Order(3)
	public void findCollegeByMajorTest(){
		// Will build later
		assertTrue(false);
	}
}
