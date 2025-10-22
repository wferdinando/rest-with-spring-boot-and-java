package br.com.wfsystems.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

import com.mysql.cj.x.protobuf.MysqlxCrud.Order.Direction;

import br.com.wfsystems.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.wfsystems.model.Person;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    PersonRepository repository;
    private static Person person;

    @BeforeAll
    static void setUp() {
        person = new Person();
    }

    @Test
    @Order(2)
    void testDisablePerson() {

        Long id = person.getId();
        repository.disablePerson(id);

        var result = repository.findById(id);
        person = result.get();

  assertNotNull(person);
        assertNotNull(person.getId());
        assertEquals("Lodovico", person.getFirstName());
        assertEquals("Moulton", person.getLastName());
        assertEquals("Male", person.getGender());
        assertTrue(!person.isEnabled());
        assertEquals("Apt 604", person.getAddress());
    }

    @Test
    @Order(1)
    void testFindPeopleByName() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.ASC, "firstName"));

        person = repository.findPeopleByName("ico", pageable).getContent().get(1);

        assertNotNull(person);
        assertNotNull(person.getId());
        assertEquals("Lodovico", person.getFirstName());
        assertEquals("Moulton", person.getLastName());
        assertEquals("Male", person.getGender());
        assertTrue(person.isEnabled());
        assertEquals("Apt 604", person.getAddress());
    }
}
