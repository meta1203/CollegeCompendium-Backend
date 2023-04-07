package com.collegecompendium.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.Degree;

import jakarta.transaction.Transactional;

@Transactional
public interface CollegeRepository extends CrudRepository<College, String>, CollegeRepositoryCustom {
	Optional<College> findById(String id);
	List<College> findByDegreesIn(Degree... degrees);
	List<College> findByDegreesIn(List<Degree> degrees);
}
