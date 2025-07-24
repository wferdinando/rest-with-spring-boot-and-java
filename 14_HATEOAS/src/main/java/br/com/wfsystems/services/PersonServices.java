package br.com.wfsystems.services;

import static br.com.wfsystems.mapper.ObjectMapper.parseListObjects;
import static br.com.wfsystems.mapper.ObjectMapper.parseObject;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.wfsystems.controllers.PersonController;
import br.com.wfsystems.data.dto.v1.PersonDTO;
import br.com.wfsystems.data.dto.v2.PersonDTOV2;
import br.com.wfsystems.exceptions.RequiredObjectIsNullException;
import br.com.wfsystems.exceptions.ResourceNotFoundException;
import br.com.wfsystems.mapper.custom.PersonMapper;
import br.com.wfsystems.model.Person;
import br.com.wfsystems.repository.PersonRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonServices {
    private Logger logger = LoggerFactory.getLogger(PersonServices.class.getName());

    @Autowired
    PersonRepository repository;

    @Autowired
    PersonMapper converter;

    public PersonDTO findById(Long id) {
        logger.info("Finding one Person!");

        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        PersonDTO personDTO = parseObject(entity, PersonDTO.class);
        addHateoasLinks(personDTO);
        return personDTO;
    }

    public List<PersonDTO> findAll() {
        logger.info("Finding All People!");

        List<PersonDTO> persons = parseListObjects(repository.findAll(), PersonDTO.class);
        persons.forEach(this::addHateoasLinks);
        return persons;
    }

    public PersonDTO create(PersonDTO person) {

        if(person == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one Person! ");
        Person entity = parseObject(person, Person.class);
        PersonDTO personDTO = parseObject(repository.save(entity), PersonDTO.class);
        addHateoasLinks(personDTO);
        return personDTO;
    }

    public PersonDTOV2 createV2(PersonDTOV2 person) {
        logger.info("Creating one Person V2! ");
        Person entity = converter.convertDtoToEntity(person);
        return converter.convertEntityToDto(repository.save(entity));
    }

    public PersonDTO update(PersonDTO person) {

        if(person == null) throw new RequiredObjectIsNullException();

        logger.info("Updating one Person! ");
        Person entity = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        PersonDTO personDTO = parseObject(repository.save(entity), PersonDTO.class);
        addHateoasLinks(personDTO);
        return personDTO;
    }

    public void delete(Long id) {
        logger.info("Deleting one Person!");
        Person person = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(person);
    }

    private void addHateoasLinks(PersonDTO personDTO) {
        personDTO.add(
                linkTo(methodOn(PersonController.class).findById(personDTO.getId())).withSelfRel().withType("GET"));
        personDTO.add(linkTo(methodOn(PersonController.class).findAll()).withRel("findAll").withType("GET"));
        personDTO.add(linkTo(methodOn(PersonController.class).create(personDTO)).withRel("create").withType("POST"));
        personDTO.add(linkTo(methodOn(PersonController.class).update(personDTO)).withRel("update").withType("PUT"));
        personDTO.add(linkTo(methodOn(PersonController.class).delete(personDTO.getId())).withRel("delete")
                .withType("DELETE"));
    }
}
