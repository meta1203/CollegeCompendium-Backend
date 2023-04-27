package com.collegecompendium.backend.repositories;

import com.collegecompendium.backend.models.Degree;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface DegreeRepository extends CrudRepository<Degree, String> {
}
