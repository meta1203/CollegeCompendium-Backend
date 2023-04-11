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
import org.springframework.test.context.ActiveProfiles;

import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.Location;
import com.collegecompendium.backend.models.Photo;
import com.collegecompendium.backend.repositories.CollegeRepository;

import lombok.extern.log4j.Log4j2;

@SpringBootTest
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Log4j2
public class CollegeTests {
    @Autowired
    private CollegeRepository collegeRepository; // CRUD repo for database

    @Test
    @Order(3)
    void testCollegeRepo(){
    	College input = College.builder()
    			.name("New Mexico Tech")
    			.inStateCost(10000)
    			.outStateCost(20000)
    			.location(new Location("34.066017", "-106.905613"))
    			.url("https://www.nmt.edu/")
                .photos(List.of(
                        Photo.builder().url("https://imgur.com/F7DlWnf").build(),
                        Photo.builder().url("https://imgur.com/funxCwh").build()
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
        assertEquals(input.getPhotos().get(0).getUrl(), output.getPhotos().get(0).getUrl());
        assertEquals(input.getPhotos().get(1).getUrl(), output.getPhotos().get(1).getUrl());
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
    	
    	Location nearby = new Location("Neel Dr, Socorro, NM 87801", "34.063226", "-106.905866");
    	
    	List<College> nearbyColleges = collegeRepository.findAllCollegesNear(nearby, 1000);
    	assertNotNull(nearbyColleges);
    	assertFalse(nearbyColleges.isEmpty());
    	log.warn(nearbyColleges.stream().map(c -> c.getName()).collect(Collectors.joining("[ ", ", ", " ]")));
    	log.warn(nearbyColleges.stream().flatMap(c -> c.getDegrees().stream()).map(deg -> deg.getName()).collect(Collectors.joining(", ")));
    }
}
