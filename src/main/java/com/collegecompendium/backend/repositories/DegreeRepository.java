package com.collegecompendium.backend.repositories;

import com.collegecompendium.backend.models.Major;
import org.springframework.data.repository.CrudRepository;

import com.collegecompendium.backend.models.Degree;

public interface DegreeRepository extends CrudRepository<Degree, String>{
    public Degree findByName(String name);


}
