package br.com.wfsystems.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.wfsystems.data.dto.v1.BookDTO;
import br.com.wfsystems.exceptions.RequiredObjectIsNullException;
import br.com.wfsystems.model.Book;
import br.com.wfsystems.repository.BookRepository;
import br.com.wfsystems.services.BookServices;
import br.com.wfsystems.unittests.mapper.MockBook;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class BookServicesTest {

    MockBook input;

    @InjectMocks
    private BookServices service;

    @Mock
    BookRepository repository;

    @BeforeEach
    void setUp() {
        input = new MockBook();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById() {
        Book Book = input.mockEntity(1);
        Book.setId(1);
        when(repository.findById(1)).thenReturn(Optional.ofNullable(Book));
        var result = service.findById(1);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("api/book/v1/1")
                        && link.getType().equals("GET")));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("api/book/v1")
                        && link.getType().equals("GET")));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("api/book/v1")
                        && link.getType().equals("POST")));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("api/book/v1")
                        && link.getType().equals("PUT")));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("api/book/v1/1")
                        && link.getType().equals("DELETE")));

        assertEquals("Some Author1", result.getAuthor());
        assertNotNull(result.getLaunchDate());
        assertEquals(25D, result.getPrice());
        assertEquals("Some Title1", result.getTitle());

    }

    @Test
    void testCreate() {

        Book Book = input.mockEntity(1);
        Book persisted = Book;
        persisted.setId(1);

        BookDTO BookDTO = input.mockDTO(1);

        when(repository.save(Book)).thenReturn(persisted);

        var result = service.create(BookDTO);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("api/book/v1/1")
                        && link.getType().equals("GET")));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("api/book/v1")
                        && link.getType().equals("GET")));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("api/book/v1")
                        && link.getType().equals("POST")));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("api/book/v1")
                        && link.getType().equals("PUT")));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("api/book/v1/1")
                        && link.getType().equals("DELETE")));

        assertEquals("Some Author1", result.getAuthor());
        assertNotNull(result.getLaunchDate());
        assertEquals(25D, result.getPrice());
        assertEquals("Some Title1", result.getTitle());

    }

    @Test
    void testCreateWithNullBook() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.create(null);
        });

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUpdate() {
        Book Book = input.mockEntity(1);
        Book persisted = Book;
        persisted.setId(1);

        BookDTO BookDTO = input.mockDTO(1);

        when(repository.findById(1)).thenReturn(Optional.ofNullable(Book));
        var result = service.update(BookDTO);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("api/book/v1/4")
                        && link.getType().equals("GET")));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("api/book/v1")
                        && link.getType().equals("GET")));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("api/book/v1")
                        && link.getType().equals("POST")));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("api/book/v1")
                        && link.getType().equals("PUT")));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("api/book/v1/1")
                        && link.getType().equals("DELETE")));

        assertEquals("Some Author1", result.getAuthor());
        assertNotNull(result.getLaunchDate());
        assertEquals(25D, result.getPrice());
        assertEquals("Some Title1", result.getTitle());
    }

    @Test
    void testUpdateWithNullBook() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.update(null);
        });

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDelete() {

        Book Book = input.mockEntity(1);
        Book.setId(1);
        when(repository.findById(1)).thenReturn(Optional.ofNullable(Book));

        service.delete(1);
        verify(repository, times(1)).findById(anyInt());
        verify(repository, times(1)).delete(any(Book.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testFindAll() {
        List<Book> list = input.mockEntityList();
        when(repository.findAll()).thenReturn(list);
        List<BookDTO> people = service.findAll();

        assertNotNull(people);
        assertEquals(14, people.size());

        var BookOne = people.get(1);

        assertNotNull(BookOne.getId());
        assertNotNull(BookOne.getLinks());

        assertNotNull(BookOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("api/book/v1/1")
                        && link.getType().equals("GET")));

        assertNotNull(BookOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("api/book/v1")
                        && link.getType().equals("GET")));

        assertNotNull(BookOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("api/book/v1")
                        && link.getType().equals("POST")));

        assertNotNull(BookOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("api/book/v1")
                        && link.getType().equals("PUT")));

        assertNotNull(BookOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("api/book/v1/1")
                        && link.getType().equals("DELETE")));

        assertEquals("Some Author1", BookOne.getAuthor());
        assertNotNull(BookOne.getLaunchDate());
        assertEquals(25D, BookOne.getPrice());
        assertEquals("Some Title1", BookOne.getTitle());

        var BookFour = people.get(4);

        assertNotNull(BookFour.getId());
        assertNotNull(BookFour.getLinks());

        assertNotNull(BookFour.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("api/book/v1/4")
                        && link.getType().equals("GET")));

        assertNotNull(BookFour.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("api/book/v1")
                        && link.getType().equals("GET")));

        assertNotNull(BookFour.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("api/book/v1")
                        && link.getType().equals("POST")));

        assertNotNull(BookFour.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("api/book/v1")
                        && link.getType().equals("PUT")));

        assertNotNull(BookFour.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("api/book/v1/4")
                        && link.getType().equals("DELETE")));

        assertEquals("Some Author4", BookFour.getAuthor());
        assertNotNull(BookFour.getLaunchDate());
        ;
        assertEquals(25D, BookFour.getPrice());
        assertEquals("Some Title4", BookFour.getTitle());
    }
}
