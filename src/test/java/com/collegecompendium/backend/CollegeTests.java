package com.collegecompendium.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.Location;
import com.collegecompendium.backend.models.Student;
import com.collegecompendium.backend.repositories.CollegeRepository;
import com.collegecompendium.backend.repositories.StudentRepository;

import lombok.extern.log4j.Log4j2;

@SpringBootTest
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Log4j2
public class CollegeTests {
    @Autowired
    private CollegeRepository collegeRepository; // CRUD repo for database
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private Jwt injectedToken;

    @Test
    @Order(3)
    void testCollegeRepo(){
    	College input = College.builder()
    			.name("New Mexico Tech")
    			.inStateCost(10000)
    			.outStateCost(20000)
    			.location(new Location("34.066017", "-106.905613"))
    			.url("https://www.nmt.edu/")
    			.build();
    	College output = collegeRepository.save(input);
    	
    	assertEquals(input.getLocation(), output.getLocation());
    	assertEquals(input.getUrl(), output.getUrl());
    	assertNotNull(input.getId());
    }

    @Test
    @Order(4)
    void testCollegeController() {

    }
    
    @Test
    @Order(10)
    void testCollegeRange() {
    	College unm = College.builder()
    			.name("University of New Mexico")
    			.inStateCost(20000)
    			.outStateCost(40000)
    			.location(new Location("35.084508", "-106.619423"))
    			.url("https://www.unm.edu/")
    			.build();
    	unm = collegeRepository.save(unm);
    	
    	Student ourGuy = studentRepository.findDistinctByAuth0Id(injectedToken.getSubject());
    	assertNotNull(ourGuy);
    	
    	List<College> nearbyColleges = collegeRepository.findAllCollegesNear(ourGuy.getLocation(), 100000);
    	assertNotNull(nearbyColleges);
    	assertFalse(nearbyColleges.isEmpty());
    	log.warn(nearbyColleges.stream().map(c -> c.toString()).collect(Collectors.joining("[ ", ", ", " ]")));
    }
}
