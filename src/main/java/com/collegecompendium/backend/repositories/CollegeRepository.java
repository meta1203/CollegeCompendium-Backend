package com.collegecompendium.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.Degree;
import com.collegecompendium.backend.models.Location;

import jakarta.transaction.Transactional;

@Transactional
public interface CollegeRepository extends CrudRepository<College, String>{
	Optional<College> findById(String id);
	List<College> findByDegreesIn(Degree... degrees);
	List<College> findByDegreesIn(List<Degree> degrees);
	
	@Query("select c from College c " + 
	"where " + 
	"c.latitude BETWEEN :location.latitude - :range AND :location.latitude + :range " +
	"AND c.longitude BETWEEN :location.longitude - :range AND :location.longitude - :range"
	)
	public List<College> findAllCollegesNear(
			@Param("location") Location location,
			@Param("range") Integer range
	);
}
