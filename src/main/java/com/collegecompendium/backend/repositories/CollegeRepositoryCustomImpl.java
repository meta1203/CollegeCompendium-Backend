package com.collegecompendium.backend.repositories;

import java.util.List;

import com.collegecompendium.backend.models.College;
import com.collegecompendium.backend.models.Location;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import jakarta.persistence.TypedQuery;

public class CollegeRepositoryCustomImpl implements CollegeRepositoryCustom {
	// TODO: can we get lazy loading without using an extended persistance context?
	@PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;
	
	@Override
	public List<College> findAllCollegesNear(Location location, Integer range) {
		// convert range from miles to degrees
		range = range * 1000000 / 69;
		
		double lat = location.getLatitude();
		double lng = location.getLongitude();
		
		TypedQuery<College> tq = entityManager.createQuery(
			"SELECT c FROM College c " +
			"WHERE " +
			"(c.location.latitude BETWEEN :latl AND :lath) " + 
			"AND" + 
			"(c.location.longitude BETWEEN :longl AND :longh) ",
			College.class
		)
		.setParameter("latl", lat - range)
		.setParameter("lath", lat + range)
		.setParameter("longl", lng - range)
		.setParameter("longh", lng + range);
		
		return tq.getResultList();
	}

}
