package com.collegecompendium.backend.repositories;

import com.collegecompendium.backend.models.Degree;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DegreeRepository extends CrudRepository<Degree, String>{
    public Degree findByName(String name);
}
