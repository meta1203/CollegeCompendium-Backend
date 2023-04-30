package com.collegecompendium.backend.repositories;

import com.collegecompendium.backend.models.Degree;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

@Transactional
/**
 * Repository for Degree, provides CRUD operations
 */
public interface DegreeRepository extends CrudRepository<Degree, String> {
}
