package br.com.wfsystems.services;

import static br.com.wfsystems.mapper.ObjectMapper.parseListObjects;
import static br.com.wfsystems.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.wfsystems.controllers.BookController;
import br.com.wfsystems.data.dto.v1.BookDTO;
import br.com.wfsystems.exceptions.RequiredObjectIsNullException;
import br.com.wfsystems.exceptions.ResourceNotFoundException;
import br.com.wfsystems.model.Book;
import br.com.wfsystems.repository.BookRepository;

@Service
public class BookServices {

    @Autowired
    BookRepository repository;

    private Logger logger = LoggerFactory.getLogger(PersonServices.class.getName());

    public BookDTO findById(Integer id) {

        logger.info("Finding one Book!");

        Book entitity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        BookDTO bookDTO = parseObject(entitity, BookDTO.class);

        addHateoasLinks(bookDTO);
        return bookDTO;
    }

    public List<BookDTO> findAll() {
        logger.info("Finding all Books!");

        List<BookDTO> books = parseListObjects(repository.findAll(), BookDTO.class);
        books.forEach(this::addHateoasLinks);
        return books;
    }

    public BookDTO create(BookDTO book) {

        if (book == null)
            throw new RequiredObjectIsNullException();

        logger.info("Creating one Book! ");
        Book entity = parseObject(book, Book.class);
        BookDTO bookDTO = parseObject(repository.save(entity), BookDTO.class);

        addHateoasLinks(bookDTO);
        return bookDTO;
    }

    public BookDTO update(BookDTO book) {

        if (book == null)
            throw new RequiredObjectIsNullException();

        logger.info("Updating one Book! ");
        Book entity = repository.findById(book.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        entity.setAuthor(book.getAuthor());
        entity.setLaunchDate(book.getLaunchDate());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());

        BookDTO bookDTO = parseObject(entity, BookDTO.class);

        addHateoasLinks(bookDTO);
        return bookDTO;
    }

    public void delete(Integer id) {
        logger.info("Deleting one Book! ");
        Book entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(entity);
    }

    private void addHateoasLinks(BookDTO bookDTO) {
        bookDTO.add(
                linkTo(methodOn(BookController.class).findById(bookDTO.getId())).withSelfRel().withType("GET"));
        bookDTO.add(linkTo(methodOn(BookController.class).findAll()).withRel("findAll").withType("GET"));
        bookDTO.add(linkTo(methodOn(BookController.class).create(bookDTO)).withRel("create").withType("POST"));
        bookDTO.add(linkTo(methodOn(BookController.class).update(bookDTO)).withRel("update").withType("PUT"));
        bookDTO.add(linkTo(methodOn(BookController.class).delete(bookDTO.getId())).withRel("delete")
                .withType("DELETE"));
    }

}
