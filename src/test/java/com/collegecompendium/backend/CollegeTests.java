package com.collegecompendium.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import com.collegecompendium.backend.models.Degree;
import com.collegecompendium.backend.models.Location;
import com.collegecompendium.backend.models.Major;
import com.collegecompendium.backend.models.User;
import com.collegecompendium.backend.repositories.CollegeAdminRepository;
import com.collegecompendium.backend.repositories.CollegeRepository;
import com.collegecompendium.backend.repositories.DegreeRepository;
import com.collegecompendium.backend.repositories.MajorRepository;

import lombok.extern.log4j.Log4j2;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Log4j2
public class CollegeTests {

	@Autowired
	private CollegeRepository collegeRepository; // CRUD repo for database
	@Autowired
	private CollegeAdminRepository collegeAdminRepository;
	@Autowired 
	private MajorRepository majorRepository;
	@Autowired 
	private DegreeRepository degreeRepository;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private Jwt injectedJwt;
	@Autowired
	private UserProvider userProvider;

	private College testCollege = null;
	
	// reset the user before each test to ensure we have a
	// common set of data between tests
	@BeforeEach
	void setup() {
		// collegeRepository.findAll().forEach(c -> collegeRepository.delete(c));

		// prepopulate college
		testCollege = College.builder()
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
				// create new object if the testCollege doesn't already exist,
				// update the testCollege object back to default if it does
				.id(testCollege != null ? testCollege.getId() : null)
				.url("https://www.nmt.edu/")
				.build();
		testCollege = collegeRepository.save(testCollege);

		User user = userProvider.getUserForToken(injectedJwt);
		if (user != null) userProvider.deleteUser(user);

		CollegeAdmin me = CollegeAdmin.builder()
				.auth0Id(injectedJwt.getSubject())
				.college(testCollege)
				.email("test@example.com")
				.firstName("T")
				.lastName("St")
				.middleInitial("E")
				.username("test")
				.approved(true)
				.build();
		me = collegeAdminRepository.save(me);
	}

	@Test
	@Order(3)
	void testCollegeRepo() {
		College input = College.builder()
				.name("New Mexico Tech")
				.inStateCost(10000)
				.outStateCost(20000)
				.location(new Location("", 34.066017, -106.905613))
				.url("https://www.nmt.edu/")
				.photos(List.of(
						"https://imgur.com/F7DlWnf",
						"https://imgur.com/funxCwh"
						))
				.phoneNumber("555-123-4567")
				.description("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
				.popularity(69)
				.build();
		College output = collegeRepository.save(input);

		assertEquals(input.getLocation(), output.getLocation());
		assertEquals(input.getUrl(), output.getUrl());
		assertEquals(input.getPhoneNumber(), output.getPhoneNumber());
		assertEquals(input.getDescription(), output.getDescription());
		assertEquals(input.getPopularity(), output.getPopularity());
		assertEquals(input.getPhotos().size(), output.getPhotos().size());
		assertEquals(input.getPhotos().get(0), output.getPhotos().get(0));
		assertEquals(input.getPhotos().get(1), output.getPhotos().get(1));
		assertNotNull(input.getId());
		
		collegeRepository.delete(input);
	}

	@Test
	@Order(4)
	void testApproveCollegeAdmin() {
		String testEmail = "ElonMusk@bitch.com";

		CollegeAdmin target = CollegeAdmin.builder()
				.college(testCollege)
				.auth0Id("asdf")
				.approved(false)
				.email(testEmail)
				.firstName("Eeby")
				.lastName("Deeby")
				.username("asdf")
				.build();
		target = collegeAdminRepository.save(target);

		RequestEntity<Void> request = RequestEntity
				.post(URI.create("http://localhost:8080/collegeAdmin/approve/" + target.getEmail()))
				.header("Authorization", "Bearer " + injectedJwt.getTokenValue())
				.build();

		ResponseEntity<CollegeAdmin> resp = restTemplate.exchange(request, CollegeAdmin.class);

		assertEquals(HttpStatus.OK, resp.getStatusCode());

		// Delete
		collegeAdminRepository.delete(target);
	}

	@Test
	@Order(5)
	void testUnapprovedAdminAccess() {

		CollegeAdmin me = (CollegeAdmin)userProvider.getUserForToken(injectedJwt);
		me.setApproved(false);
		collegeAdminRepository.save(me);

		CollegeAdmin target = CollegeAdmin.builder()
				.college(testCollege)
				.auth0Id("asdf")
				.approved(false)
				.email("check@gmail.com")
				.firstName("Eeby")
				.lastName("Deeby")
				.username("Monty")
				.build();
		collegeAdminRepository.save(target);

		// attempt to approve a college admin with the unapproved college admin user
		RequestEntity<Void> approveAdminRequest = RequestEntity
				.post(URI.create("http://localhost:8080/collegeAdmin/approve/elonmusk@example.com"))
				.header("Authorization", "Bearer " + injectedJwt.getTokenValue())
				.build();
		ResponseEntity<Void> approveAdminResponse = restTemplate
				.exchange(approveAdminRequest, Void.class);


		// verify that a response returns a 403 Forbidden status code
		assertEquals(HttpStatus.FORBIDDEN, approveAdminResponse.getStatusCode());

		//Delete stuff
		collegeAdminRepository.delete(target);
		//  collegeAdminRepository.delete(unapprovedAdmin);
	}

	@Test
	@Order(6)
	void testUnapprovedDeleteAdmin() {

		CollegeAdmin targetDelete = CollegeAdmin.builder()
				.college(testCollege)
				.auth0Id("deleteAuth0Id")
				.approved(false)
				.email("DeleteMe@gmail.com")
				.firstName("Eeby")
				.lastName("Deeby")
				.username("Unique Username Broseph")
				.build();
		collegeAdminRepository.save(targetDelete);

		RequestEntity<Void> deleteAdminRequest = RequestEntity
				.delete(URI.create("http://localhost:8080/collegeAdmin/" + targetDelete.getId()))
				.header("Authorization", "Bearer " + injectedJwt.getTokenValue())
				.build();
		ResponseEntity<Void> deleteAdminResponse = restTemplate
				.exchange(deleteAdminRequest, Void.class);

		assertEquals(HttpStatus.FORBIDDEN, deleteAdminResponse.getStatusCode());

		// Delete stuff
		collegeAdminRepository.delete(targetDelete);
	}
	
	@Test
	@Order(7)
	void testCollegeUpdate() {
		final String NEW_NAME = "new name";
		testCollege.setName(NEW_NAME);
		RequestEntity<College> updateCollegeRequest = RequestEntity
				.put(URI.create("http://localhost:8080/collegeAdmin/college"))
				.header("Authorization", "Bearer " + injectedJwt.getTokenValue())
				.body(testCollege);
		ResponseEntity<College> updateCollegeResponse = restTemplate
				.exchange(updateCollegeRequest, College.class);
		
		assertEquals(updateCollegeResponse.getStatusCode(), HttpStatus.OK);
		College resp = updateCollegeResponse.getBody();
		assertEquals(resp.getName(), NEW_NAME);
	}

	@Test
	@Order(10)
	void testCollegeRange() {
		College unm = College.builder()
				.name("University of New Mexico")
				.inStateCost(20000)
				.outStateCost(40000)
				.location(new Location("", 35.084508, -106.619423))
				.url("https://www.unm.edu/")
				.build();
		unm = collegeRepository.save(unm);

		Location nearby = new Location("Neel Dr, Socorro, NM 87801", "34.063226", "-106.905866");

		List<College> nearbyColleges = collegeRepository.findAllCollegesNear(nearby, 1d);
		assertNotNull(nearbyColleges);
		assertFalse(nearbyColleges.isEmpty());
		// assertEquals(1, nearbyColleges.size());
		log.warn(nearbyColleges.stream().map(c -> c.getName()).collect(Collectors.joining(", ", "[ ", " ]")));
		// log.warn(nearbyColleges.stream().flatMap(c -> c.getDegrees().stream()).map(deg -> deg.getName()).collect(Collectors.joining(", ")));
		collegeRepository.delete(unm);
	}

	@Test
	@Order(11)
	public void addDegreeToCollegeTest(){
		Major m = Major.builder()
				.name("Computer Science")
				.majorType(Major.MajorType.BUSINESS)
				.build();
		m = majorRepository.save(m);
		Degree d = Degree.builder()
				.degreeType(Degree.DegreeType.BACHELOR)
				.creditsRequired(120)
				.major(m)
				.build();

		// Add the degree to the college
		RequestEntity<Degree> re = RequestEntity
				.post("http://localhost:8080/collegeAdmin/college/degree")
				.header("Authorization", "Bearer " + injectedJwt.getTokenValue())
				.body(d);
		ResponseEntity<Degree> resp = restTemplate.exchange(re, Degree.class);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Degree saved = resp.getBody();

		// Verify that the degree was added to the college
		assertEquals(d.getDegreeType(), saved.getDegreeType());
		assertEquals(d.getCreditsRequired(), saved.getCreditsRequired());
		assertEquals(d.getMajor().getName(), saved.getMajor().getName());
		assertEquals(d.getMajor().getMajorType(), saved.getMajor().getMajorType());
		assertEquals(d.getMajor().getId(), saved.getMajor().getId());
	}

	// @Test
	@Order(12)
	public void removeDegreeFromCollegeTest(){
		assertTrue(false);
	}

//    @Autowired
//    private CollegeRepository collegeRepository; // CRUD repo for database
//    @Autowired
//    private CollegeAdminRepository collegeAdminRepository;
//    @Autowired
//    private Jwt injectedJwt;
//    @Autowired
//    private UserProvider userProvider;
//    
//    // reset the user before each test to ensure we have a
//    // common set of data between tests
//    @BeforeEach
//    void setup() {
//    	User user = userProvider.getUserForToken(injectedJwt);
//    	if (user != null) userProvider.deleteUser(user);
//    	
//    	CollegeAdmin me = CollegeAdmin.builder()
//    			.auth0Id(injectedJwt.getSubject())
//    			.college(null)
//    			.email("test@example.com")
//    			.firstName("T")
//    			.lastName("St")
//    			.middleInitial("E")
//    			.username("test")
//    			.build();
//    	me = collegeAdminRepository.save(me);
//    }
//
//    @Test
//    @Order(3)
//    void testCollegeRepo() {
//    	College input = College.builder()
//    			.name("New Mexico Tech")
//    			.inStateCost(10000)
//    			.outStateCost(20000)
//    			.location(new Location(34.066017d, -106.905613d))
//    			.url("https://www.nmt.edu/")
//                .photos(List.of(
//                        "https://imgur.com/F7DlWnf",
//                        "https://imgur.com/funxCwh"
//                ))
//                .phoneNumber("555-123-4567")
//                .description("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
//                .popularity(69)
//    			.build();
//    	College output = collegeRepository.save(input);
//    	
//    	assertEquals(input.getLocation(), output.getLocation());
//    	assertEquals(input.getUrl(), output.getUrl());
//    	assertEquals(input.getPhoneNumber(), output.getPhoneNumber());
//        assertEquals(input.getDescription(), output.getDescription());
//        assertEquals(input.getPopularity(), output.getPopularity());
//        assertEquals(input.getPhotos().size(), output.getPhotos().size());
//        assertEquals(input.getPhotos().get(0), output.getPhotos().get(0));
//        assertEquals(input.getPhotos().get(1), output.getPhotos().get(1));
//    	assertNotNull(input.getId());
//    }
//
//    @Test
//    @Order(4)
//    void testCollegeController() {
//
//    }
//    
//    @Test.
//    @Order(10)
//    void testCollegeRange() {
//    	College unm = College.builder()
//    			.name("University of New Mexico")
//    			.inStateCost(20000)
//    			.outStateCost(40000)
//    			.location(new Location(35.084508d, -106.619423d))
//    			.url("https://www.unm.edu/")
//    			.build();
//    	unm = collegeRepository.save(unm);
//    	
//    	Location nearby = new Location("Neel Dr, Socorro, NM 87801", "34.063226", "-106.905866");
//    	
//    	List<College> nearbyColleges = collegeRepository.findAllCollegesNear(nearby, 1000);
//    	assertNotNull(nearbyColleges);
//    	assertFalse(nearbyColleges.isEmpty());
//    	log.warn(nearbyColleges.stream().map(c -> c.getName()).collect(Collectors.joining("[ ", ", ", " ]")));
//    	// log.warn(nearbyColleges.stream().flatMap(c -> c.getDegrees().stream()).map(deg -> deg.getName()).collect(Collectors.joining(", ")));
//    }
}
