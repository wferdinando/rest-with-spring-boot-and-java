package br.com.wfsystems.services;

import static br.com.wfsystems.mapper.ObjectMapper.parseListObjects;
import static br.com.wfsystems.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.wfsystems.controllers.PersonController;
import br.com.wfsystems.data.dto.v1.PersonDTO;
import br.com.wfsystems.exceptions.RequiredObjectIsNullException;
import br.com.wfsystems.exceptions.ResourceNotFoundException;
import br.com.wfsystems.mapper.custom.PersonMapper;
import br.com.wfsystems.model.Person;
import br.com.wfsystems.repository.PersonRepository;
import jakarta.transaction.Transactional;

@Service
public class PersonServices {
    private Logger logger = LoggerFactory.getLogger(PersonServices.class.getName());

    @Autowired
    PersonRepository repository;

    @Autowired
    PersonMapper converter;

    @Autowired
    PagedResourcesAssembler<PersonDTO> assembler;

    public PersonDTO findById(Long id) {
        logger.info("Finding one Person!");

        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        PersonDTO personDTO = parseObject(entity, PersonDTO.class);
        addHateoasLinks(personDTO);
        return personDTO;
    }

    public PagedModel<EntityModel<PersonDTO>> findAll(Pageable pageable) {
        logger.info("Finding All People!");

        Page<Person> people = repository.findAll(pageable);
        var peopleWithLinks = people.map(person -> {
            PersonDTO personDTO = parseObject(person, PersonDTO.class);
            addHateoasLinks(personDTO);
            return personDTO;
        });

        Link findAllLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class)
                .findAll(pageable.getPageNumber(), pageable.getPageSize(), String.valueOf(pageable.getSort())))
                .withSelfRel();

        return assembler.toModel(peopleWithLinks, findAllLink);
    }

    public PersonDTO create(PersonDTO person) {

        if (person == null)
            throw new RequiredObjectIsNullException();

        logger.info("Creating one Person! ");
        Person entity = parseObject(person, Person.class);
        PersonDTO personDTO = parseObject(repository.save(entity), PersonDTO.class);
        addHateoasLinks(personDTO);
        return personDTO;
    }

    public PersonDTO update(PersonDTO person) {

        if (person == null)
            throw new RequiredObjectIsNullException();

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

    @Transactional
    public PersonDTO disablePerson(Long id) {
        logger.info("Disabling one Person!");
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.disablePerson(id);

        Person entity = repository.findById(id).get();
        PersonDTO dto = parseObject(entity, PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
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
        personDTO
                .add(linkTo(methodOn(PersonController.class).findAll(1, 12, "asc")).withRel("findAll").withType("GET"));
        personDTO.add(linkTo(methodOn(PersonController.class).create(personDTO)).withRel("create").withType("POST"));
        personDTO.add(linkTo(methodOn(PersonController.class).update(personDTO)).withRel("update").withType("PUT"));
        personDTO.add(linkTo(methodOn(PersonController.class).disablePerson(personDTO.getId())).withRel("disable")
                .withType("PATCH"));
        personDTO.add(linkTo(methodOn(PersonController.class).delete(personDTO.getId())).withRel("delete")
                .withType("DELETE"));
    }
}
