package br.com.wfsystems.integrationtests.controllers.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.wfsystems.config.TestConfigs;
import br.com.wfsystems.integrationtests.dto.PersonDTO;
import br.com.wfsystems.integrationtests.dto.wrappers.WrapperPersonDTO;
import br.com.wfsystems.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerJsonTest extends AbstractIntegrationTest {

        private static RequestSpecification specification;
        private static ObjectMapper objectMapper;
        private static PersonDTO person;

        @BeforeAll
        static void setUp() {

                objectMapper = new ObjectMapper();
                objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

                person = new PersonDTO();
        }

        @Test
        @Order(1)
        void testCreate() throws JsonMappingException, JsonProcessingException {
                mockPerson();

                specification = new RequestSpecBuilder()
                                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_WFERDINANDO)
                                .setBasePath("/api/person/v1")
                                .setPort(TestConfigs.SERVER_PORT)
                                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                                .build();

                var content = given(specification)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .body(person)
                                .when()
                                .post()
                                .then()
                                .statusCode(200)
                                .extract()
                                .body()
                                .asString();

                PersonDTO createPerson = objectMapper.readValue(content,
                                br.com.wfsystems.integrationtests.dto.PersonDTO.class);
                person = createPerson;

                assertNotNull(createPerson.getId());
                assertTrue(createPerson.getId() > 0);

                assertEquals("Linus", createPerson.getFirstName());
                assertEquals("Torvalds", createPerson.getLastName());
                assertEquals("Helsinki - Finland", createPerson.getAddress());
                assertEquals("Male", createPerson.getGender());
                assertTrue(createPerson.isEnabled());

        }

        @Test
        @Order(2)
        void testUpdate() throws JsonMappingException, JsonProcessingException {
                person.setLastName("Benedict Torvalds");

                var content = given(specification)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .body(person)
                                .when()
                                .put()
                                .then()
                                .statusCode(200)
                                .extract()
                                .body()
                                .asString();

                PersonDTO createPerson = objectMapper.readValue(content,
                                br.com.wfsystems.integrationtests.dto.PersonDTO.class);
                person = createPerson;

                assertNotNull(createPerson.getId());
                assertTrue(createPerson.getId() > 0);

                assertEquals("Linus", createPerson.getFirstName());
                assertEquals("Benedict Torvalds", createPerson.getLastName());
                assertEquals("Helsinki - Finland", createPerson.getAddress());
                assertEquals("Male", createPerson.getGender());
                assertTrue(createPerson.isEnabled());

        }

        @Test
        @Order(3)
        void testFindById() throws JsonMappingException, JsonProcessingException {

                var content = given(specification)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .pathParam("id", person.getId())
                                .when()
                                .get("{id}")
                                .then()
                                .statusCode(200)
                                .extract()
                                .body()
                                .asString();

                PersonDTO createPerson = objectMapper.readValue(content,
                                br.com.wfsystems.integrationtests.dto.PersonDTO.class);
                person = createPerson;

                assertNotNull(createPerson.getId());
                assertTrue(createPerson.getId() > 0);

                assertEquals("Linus", createPerson.getFirstName());
                assertEquals("Benedict Torvalds", createPerson.getLastName());
                assertEquals("Helsinki - Finland", createPerson.getAddress());
                assertEquals("Male", createPerson.getGender());
                assertTrue(createPerson.isEnabled());
        }

        @Test
        @Order(6)
        void findAllTest() throws JsonProcessingException {

                var content = given(specification)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .queryParams("page", 3, "size", 12, "direction", "asc")
                                .when()
                                .get()
                                .then()
                                .statusCode(200)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .extract()
                                .body()
                                .asString();

                WrapperPersonDTO wrapper = objectMapper.readValue(content, WrapperPersonDTO.class);
                List<PersonDTO> people = wrapper.getEmbedded().getPeople();

                PersonDTO personOne = people.get(0);

                assertNotNull(personOne.getId());
                assertTrue(personOne.getId() > 0);

                assertEquals("Alta", personOne.getFirstName());
                assertEquals("Oboy", personOne.getLastName());
                assertEquals("Suite 39", personOne.getAddress());
                assertEquals("Female", personOne.getGender());
                assertTrue(personOne.isEnabled());
        }

        @Test
        @Order(7)
        void findByNameTest() throws JsonProcessingException {
                //{{baseUrl}}/api/person/v1/findPeopleByName/and?page=1&size=12&direction=asc
                var content = given(specification)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .pathParam("firstName", "and")
                                .queryParams("page", 1, "size", 12, "direction", "asc")
                                .when()
                                .get("findPeopleByName/{firstName}")
                                .then()
                                .statusCode(200)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .extract()
                                .body()
                                .asString();

                WrapperPersonDTO wrapper = objectMapper.readValue(content, WrapperPersonDTO.class);
                List<PersonDTO> people = wrapper.getEmbedded().getPeople();

                PersonDTO personOne = people.get(0);

                assertNotNull(personOne.getId());
                assertTrue(personOne.getId() > 0);

                assertEquals("Farand", personOne.getFirstName());
                assertEquals("Pendrill", personOne.getLastName());
                assertEquals("Room 81", personOne.getAddress());
                assertEquals("Female", personOne.getGender());
                assertFalse(personOne.isEnabled());
        }

        @Test
        @Order(4)
        void testDisabled() throws JsonMappingException, JsonProcessingException {

                var content = given(specification)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .pathParam("id", person.getId())
                                .when()
                                .patch("{id}")
                                .then()
                                .statusCode(200)
                                .extract()
                                .body()
                                .asString();

                PersonDTO createPerson = objectMapper.readValue(content,
                                br.com.wfsystems.integrationtests.dto.PersonDTO.class);
                person = createPerson;

                assertNotNull(createPerson.getId());
                assertTrue(createPerson.getId() > 0);

                assertEquals("Linus", createPerson.getFirstName());
                assertEquals("Benedict Torvalds", createPerson.getLastName());
                assertEquals("Helsinki - Finland", createPerson.getAddress());
                assertEquals("Male", createPerson.getGender());
                assertFalse(createPerson.isEnabled());
        }

        @Test
        @Order(5)
        void deleteTest() throws JsonMappingException, JsonProcessingException {

                given(specification)
                                .pathParam("id", person.getId())
                                .when()
                                .delete("{id}")
                                .then()
                                .statusCode(204);
        }

        private void mockPerson() {
                person.setFirstName("Linus");
                person.setLastName("Torvalds");
                person.setAddress("Helsinki - Finland");
                person.setGender("Male");
                person.setEnabled(true);
        }
}
