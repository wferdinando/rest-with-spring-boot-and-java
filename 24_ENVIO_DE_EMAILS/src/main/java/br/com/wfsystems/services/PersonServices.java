package br.com.wfsystems.services;

import static br.com.wfsystems.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.wfsystems.controllers.PersonController;
import br.com.wfsystems.data.dto.v1.PersonDTO;
import br.com.wfsystems.exceptions.BadRequestException;
import br.com.wfsystems.exceptions.FileStorageException;
import br.com.wfsystems.exceptions.RequiredObjectIsNullException;
import br.com.wfsystems.exceptions.ResourceNotFoundException;
import br.com.wfsystems.file.exporter.contract.PersonExporter;
import br.com.wfsystems.file.exporter.factory.FileExporterFactory;
import br.com.wfsystems.file.importer.contract.FileImporter;
import br.com.wfsystems.file.importer.factory.FileImporterFactory;
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

    @Autowired
    FileImporterFactory importer;

    @Autowired
    FileExporterFactory exporter;

    public Resource exportPerson(Long id, String acceptHeader) {

        logger.info("Exporting data of one Person !");
        var person = repository.findById(id)
                .map(entity -> parseObject(entity, PersonDTO.class))
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        try {
            PersonExporter exporter = this.exporter.getExporter(acceptHeader);
            return exporter.exportPerson(person);
        } catch (Exception e) {
            throw new RuntimeException("Error during file export!", e);
        }
    }

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

        return buildPagedModel(pageable, people);
    }

    public PagedModel<EntityModel<PersonDTO>> findByName(String firstName, Pageable pageable) {
        logger.info("Finding People by Name!");

        Page<Person> people = repository.findPeopleByName(firstName, pageable);
        return buildPagedModel(pageable, people);
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

    public Resource exportPage(Pageable pageable, String acceptHeader) {
        logger.info("Exporting a People page!");

        List<PersonDTO> people = repository.findAll(pageable)
                .map(person -> parseObject(person, PersonDTO.class))
                .getContent();
        PersonExporter exporter;
        try {
            exporter = this.exporter.getExporter(acceptHeader);
            return exporter.exportPeople(people);
        } catch (Exception e) {
            throw new RuntimeException("Error during file export!", e);
        }
    }

    public List<PersonDTO> massCreation(MultipartFile file) {
        logger.info("Importing People from file!");

        if (file.isEmpty())
            throw new BadRequestException("Please set a Valid File!");

        try (InputStream inputStream = file.getInputStream()) {
            String filename = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new BadRequestException("File name cannot be null"));
            FileImporter importer = this.importer.getImporter(filename);

            List<Person> entities = importer.importFile(inputStream).stream()
                    .map(dto -> repository.save(parseObject(dto, Person.class)))
                    .toList();

            return entities.stream()
                    .map(entity -> {
                        var dto = parseObject(entity, PersonDTO.class);
                        addHateoasLinks(dto);
                        return dto;
                    })
                    .toList();
        } catch (Exception e) {
            throw new FileStorageException("Error processing the file!");
        }
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
        personDTO.add(linkTo(methodOn(PersonController.class).findByName("", 1, 12, "asc")).withRel("findByName")
                .withType("GET"));
        personDTO.add(linkTo(methodOn(PersonController.class).create(personDTO)).withRel("create").withType("POST"));
        personDTO.add(linkTo(methodOn(PersonController.class)).slash("massCreation").withRel("massCreation")
                .withType("POST"));
        personDTO.add(linkTo(methodOn(PersonController.class).update(personDTO)).withRel("update").withType("PUT"));
        personDTO.add(linkTo(methodOn(PersonController.class).disablePerson(personDTO.getId())).withRel("disable")
                .withType("PATCH"));
        personDTO.add(linkTo(methodOn(PersonController.class).delete(personDTO.getId())).withRel("delete")
                .withType("DELETE"));
        personDTO
                .add(linkTo(methodOn(PersonController.class).exportPage(1, 12, "asc", null))
                        .withRel("exportPage").withType("GET").withTitle("Export People"));
    }

    private PagedModel<EntityModel<PersonDTO>> buildPagedModel(Pageable pageable, Page<Person> people) {
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
}
