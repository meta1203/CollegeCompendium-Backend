package com.collegecompendium.backend.controllers;

import com.collegecompendium.backend.repositories.CollegeRepository;
import com.collegecompendium.backend.repositories.DegreeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DegreeController {
    @Autowired
    private DegreeRepository degreeRepository;

    @Autowired
    private CollegeRepository collegeRepository;
}
