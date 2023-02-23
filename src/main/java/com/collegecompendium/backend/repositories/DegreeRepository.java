package com.collegecompendium.backend.repositories;

import com.collegecompendium.backend.models.Degrees;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DegreeRepository extends CrudRepository<Degrees, String>{
    public Degrees findByName(String name);
}
