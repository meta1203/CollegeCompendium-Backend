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
	List<College> findByNameContainsIgnoreCase(String name);
	List<College> findByDegreesIn(Degree... degrees);
	List<College> findByDegreesIn(List<Degree> degrees);
	
	/**
	 * Finds all colleges that have the specified major by ID
	 * @param id - a Major entity ID
	 * @return List of all colleges that have this Major
	 */
	List<College> findByDegreesMajorId(String id);
}
