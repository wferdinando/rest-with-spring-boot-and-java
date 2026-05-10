package br.com.wfsystems.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.wfsystems.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.wfsystems.model.Person;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    PersonRepository repository;
    private static Person person;

    @BeforeAll
    static void setUp() {
        person = new Person();
    }

    @Test
    @Order(1)
    void findPeopleByName() {
        Pageable pageable = PageRequest.of(
            0,
            12,
            Sort.by(Sort.Direction.ASC, "firstName"));

        person = repository.findPeopleByName("enne", pageable).getContent().get(0);

        assertNotNull(person);
        assertNotNull(person.getId());
        assertEquals("Adrienne", person.getFirstName());
        assertEquals("Begbie", person.getLastName());
        assertEquals("Female", person.getGender());
        assertTrue(person.getEnabled());
    }

    @Test
    @Order(2)
    void disablePerson() {

        Long id = person.getId();
        repository.disablePerson(id);

        var result = repository.findById(id);
        person = result.get();

        assertNotNull(person);
        assertNotNull(person.getId());
        assertEquals("Adrienne", person.getFirstName());
        assertEquals("Begbie", person.getLastName());
        assertEquals("Female", person.getGender());
        assertFalse(person.getEnabled());
    }
}
