package com.collegecompendium.backend.controllers;

import com.collegecompendium.backend.models.Major;
import com.collegecompendium.backend.repositories.MajorRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class MajorController {


    @Autowired
    private MajorRepository majorRepository;


    @GetMapping("/search/major?id=[UUID]")
    private Major getMajorById(@PathVariable String id, HttpServletResponse response) {
        Major result = majorRepository.findByMajorName(id);

        if (result == null) {
            response.setStatus(404);
            return null;
        }
        else {
            response.setStatus(200);
            return result;
        }
    }

    @GetMapping("/search/majors?name=[partial name]")
    private List<Major> getMajorByPartialName(@PathVariable String PartialName, HttpServletResponse response) {
        List<Major> result = majorRepository.findByPartialMajorName(PartialName);

        if (result == null) {
            response.setStatus(404);
            return null;
        }
        else {
            response.setStatus(200);
            return result;
        }
    }
}
