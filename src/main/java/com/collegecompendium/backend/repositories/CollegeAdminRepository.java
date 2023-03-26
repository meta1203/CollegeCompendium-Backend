package com.collegecompendium.backend.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.CollegeAdmin;

import jakarta.transaction.Transactional;

/*
 * An auto-magically managed interface controlled by Spring for accessing entities stored in the database.
 * Make one of these for any entity you have to store.
 */

// JPA annotation - makes operations performed with this repository transactional. you most likely want this
@Transactional
// this interface extends CrudRepository, which uses the generic types
// CollegeAdmin (denotes the class to provide a CRUD repo for) and String
// (denotes the ID type on the class)
public interface CollegeAdminRepository extends CrudRepository<CollegeAdmin, String>{
	// so, this doesn't actually need any defined functions to get started
	// on a basic level. If you need more than just "get by ID" (which you likely
	// will at some point), check out this web page for info on how to do that:
	// https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#jpa.query-methods.query-creation
	
	//List<CollegeAdmin> findByDegreesIn(Degree... degrees);
	//List<CollegeAdmin> findByDegreesIn(List<Degree> degrees);
	List<CollegeAdmin> findByCollege(College college);

	public CollegeAdmin findDistinctByAuth0Id(String auth0Id);
}
