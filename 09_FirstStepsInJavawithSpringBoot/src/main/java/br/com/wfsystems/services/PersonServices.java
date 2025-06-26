package br.com.wfsystems.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.wfsystems.data.dto.PersonDTO;
import br.com.wfsystems.exceptions.ResourceNotFoundException;
import static br.com.wfsystems.mapper.ObjectMapper.parseListObjects;
import static br.com.wfsystems.mapper.ObjectMapper.parseObject;
import br.com.wfsystems.model.Person;
import br.com.wfsystems.repository.PersonRepository;

@Service
public class PersonServices {
    private Logger logger = LoggerFactory.getLogger(PersonServices.class.getName());

    @Autowired
    PersonRepository repository;

    public PersonDTO findById(Long id) {
        logger.info("Finding one Person!");

        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        return parseObject(entity, PersonDTO.class);
    }

    public List<PersonDTO> findAll() {
        logger.info("Finding All People!");

        return parseListObjects(repository.findAll(), PersonDTO.class);
    }

    public PersonDTO create(PersonDTO person) {
        logger.info("Creating one Person! ");
        Person entity = parseObject(person, Person.class);
        return parseObject(repository.save(entity), PersonDTO.class);
    }

    public PersonDTO update(PersonDTO person) {
        logger.info("Updating one Person! ");
        Person entity = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        return parseObject(repository.save(entity), PersonDTO.class);
    }

    public void delete(Long id) {
        logger.info("Deleting one Person!");
        Person person = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(person);
    }
}
