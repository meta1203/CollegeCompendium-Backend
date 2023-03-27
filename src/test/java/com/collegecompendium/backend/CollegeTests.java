package com.collegecompendium.backend;

import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.Degree;
import com.collegecompendium.backend.repositories.CollegeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("dev")
public class CollegeTests {
    @Autowired
    private CollegeRepository collegeRepository; // CRUD repo for database

    @Test
    void testCollegeRepo(){

    }

    @Test
    void testCollegeController() {

    }
}
