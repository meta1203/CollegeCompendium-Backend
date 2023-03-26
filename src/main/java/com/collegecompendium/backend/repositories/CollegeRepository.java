package com.collegecompendium.backend.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.collegecompendium.backend.models.College;

import jakarta.transaction.Transactional;

@Transactional
public interface CollegeRepository extends CrudRepository<College, String>{
	Optional<College> findById(String id);
}
