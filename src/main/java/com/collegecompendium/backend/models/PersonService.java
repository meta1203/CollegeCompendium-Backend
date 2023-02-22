package com.collegecompendium.backend.models;

import com.collegecompendium.backend.repositories.PersonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service

public class PersonService {

    private PersonDao personDao;
    @Autowired
    public PersonService(PersonDao personDao) {
        this.personDao = personDao;
    }
    public int addPerson(Person person) {
        return personDao.insertPerson(person);
    }


}
