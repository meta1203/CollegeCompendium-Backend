package com.collegecompendium.backend.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import com.collegecompendium.backend.models.Student;
import jakarta.transaction.Transactional;


@Transactional
public interface StudentRepository extends CrudRepository<Student, String> {
	
	Optional<Student> findById(String id);
	public Student findDistinctByUsername(String username);
	public Student findDistinctByAuth0Id(String auth0Id);
}
