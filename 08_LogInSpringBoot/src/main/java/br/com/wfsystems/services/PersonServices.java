package br.com.wfsystems.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.wfsystems.exceptions.ResourceNotFoundException;
import br.com.wfsystems.model.Person;
import br.com.wfsystems.repository.PersonRepository;

@Service
public class PersonServices {
    private Logger logger = LoggerFactory.getLogger(PersonServices.class.getName());

    @Autowired
    PersonRepository repository;

    public Person findById(Long id) {
        logger.info("Finding one Person!");

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
    }

    public List<Person> findAll() {
        logger.info("Finding All People!");

        return repository.findAll();
    }

    public Person create(Person person) {
        logger.info("Creating one Person! ");
        return repository.save(person);
    }

    public Person update(Person person) {
        logger.info("Updating one Person! ");
        Person entity = this.findById(person.getId());

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        return repository.save(person);
    }

    public void delete(Long id) {
        logger.info("Deleting one Person!");
        Person person = this.findById(id);
        repository.delete(person);
    }
}
