package com.collegecompendium.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import com.collegecompendium.backend.configurations.UserProvider;
import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.Degree;
import com.collegecompendium.backend.models.Location;
import com.collegecompendium.backend.models.Major;
import com.collegecompendium.backend.models.Student;
import com.collegecompendium.backend.repositories.CollegeRepository;
import com.collegecompendium.backend.repositories.DegreeRepository;
import com.collegecompendium.backend.repositories.MajorRepository;
import com.collegecompendium.backend.repositories.StudentRepository;

import lombok.extern.log4j.Log4j2;


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
	private MajorRepository majorRepository;
	@Autowired
	private UserProvider userProvider;
	@Autowired
	private DegreeRepository degreeRepository;

	College c1;

	@BeforeEach
	public void setup() {
		userProvider.deleteUser(userProvider.getUserForToken(injectedJwt));
		
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
		log.debug(resp.getBody());

		List<College> colleges = resp.getBody();
		assertNotNull(colleges);
		assertFalse(colleges.isEmpty());

		colleges.forEach(c -> log.debug(c));
	}

	@Test
	@Order(2)
	public void findCollegeByIDTest(){
		String id = c1.getId(); // Get the ID of C1

		// Query the API for the college with the ID
		RequestEntity<Void> re = RequestEntity
				.get("http://localhost:8080/search/college/{id}", id)
				.header("Authorization", "Bearer " + injectedJwt.getTokenValue())
				.build();
		
		ResponseEntity<College> resp = restTemplate.exchange(re,
				new ParameterizedTypeReference<College>(){});
		
		assertTrue(resp.getStatusCode().is2xxSuccessful());
		// log.debug(resp.getBody());

		College college = resp.getBody();
		assertNotNull(college);

		// Ensure the response the same as the college expected
		assertEquals(college.getId(), id);
		assertEquals(college.getName(), c1.getName());
	}
	
	@Test
	@Order(2)
	public void findCollegeByNameTest(){
		String subname = c1.getName();
		subname = subname.substring(subname.length() - 4, subname.length());
		
		// Query the API for the college with partial name
		RequestEntity<Void> re = RequestEntity
				.get("http://localhost:8080/search/colleges?name={name}", subname)
				.header("Authorization", "Bearer " + injectedJwt.getTokenValue())
				.build();
		
		ResponseEntity<List<College>> resp = restTemplate.exchange(re,
				new ParameterizedTypeReference<List<College>>(){});
		
		assertTrue(resp.getStatusCode().is2xxSuccessful());

		List<College> colleges = resp.getBody();
		assertNotNull(colleges);
		assertFalse(colleges.isEmpty());
		// log.debug(resp.getBody().stream().map(c -> c.toString()).collect(Collectors.joining(", ")));

		// Ensure the response contains the substring we selected
		assertEquals(colleges.get(0).getId(), c1.getId());
		assertEquals(colleges.get(0).getName(), c1.getName());
	}

	@Test
	@Order(3)
	public void findCollegeByMajorTest(){

		// Create a major and degree for the college
		Major majorCS = Major.builder()
				.name("Computer Science")
				.majorType(Major.MajorType.SCIENCE)
				.build();
		majorRepository.save(majorCS);
		Degree degree = Degree.builder()
				.major(majorCS)
				.degreeType(Degree.DegreeType.BACHELOR)
				.creditsRequired(42)
				.build();
		c1.addDegree(degree);
		degreeRepository.save(degree);
		collegeRepository.save(c1);

		// Query the API for the college with the major using ID
		RequestEntity<Void> re = RequestEntity
				.get("http://localhost:8080/search/colleges/major?id={major}", majorCS.getId())
				.header("Authorization", "Bearer " + injectedJwt.getTokenValue())
				.build();
		ResponseEntity<List<College>> resp = restTemplate.exchange(re,
				new ParameterizedTypeReference<List<College>>(){});
		
		assertTrue(resp.getStatusCode().is2xxSuccessful());

		List<College> colleges = resp.getBody();
		assertNotNull(colleges);

		// Ensure the response contains the college expected
		for (College college : colleges) {
			if(college.getId().equals(c1.getId())) {
				assertEquals(c1.getName(), college.getName());
			}
			if(college.getName().equals(c1.getName())) {
				assertEquals(c1.getId(), college.getId());
			}
		}

		// Query the API for the college with the major using name
		re = RequestEntity
				.get("http://localhost:8080/search/colleges/major?name={major}", majorCS.getName())
				.header("Authorization", "Bearer " + injectedJwt.getTokenValue())
				.build();
		resp = restTemplate.exchange(re,
				new ParameterizedTypeReference<List<College>>(){});
		log.debug(resp.getBody());
		assertTrue(resp.getStatusCode().is2xxSuccessful());

		colleges = resp.getBody();
		assertNotNull(colleges);

		// Ensure the response contains the college expected
		for (College college : colleges) {
			if(college.getId().equals(c1.getId())) {
				assertEquals(c1.getName(), college.getName());
			} else if (college.getName().equals(c1.getName())) {
				assertEquals(c1.getId(), college.getId());
			}
		}
	}
}
