package com.collegecompendium.backend.repositories;

import org.springframework.data.repository.CrudRepository;

import com.collegecompendium.backend.models.Degree;

public interface DegreeRepository extends CrudRepository<Degree, String>{
}
