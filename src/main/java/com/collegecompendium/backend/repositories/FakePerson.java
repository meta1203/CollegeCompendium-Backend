package com.collegecompendium.backend.repositories;

import com.collegecompendium.backend.models.Person;
import com.collegecompendium.backend.repositories.PersonDao;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;


;
public class FakePerson implements PersonDao {

    private ArrayList<Person> PersonDatabase = new ArrayList<>();
    @Override
    public int insertPerson(UUID id, Person person) {
        PersonDatabase.add(new Person(id, person.getName(), person.getGPA(), person.getLocation()));
        return 1;
    }

}
