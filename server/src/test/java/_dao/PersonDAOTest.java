package _dao;

import _model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PersonDAOTest {
    private Database db = new Database();
    private Connection conn;
    private Person testPerson;
    private Person testPerson2;

    @BeforeEach
    public void setUp() throws Exception {
        conn = db.openConnection();
        db.clearTables();
        testPerson = new Person("katelyn123", "katelynTullis",
                "Katelyn", "Tullis", "f", "stuff", "moreStuff", "evenMoreStuff");
        testPerson2 = new Person("jonathan123", "jonathanTullis",
                "Jonny", "Tullis", "m", "stuff", "moreStuff", "evenMoreStuff");
    }

    @AfterEach
    public void tearDown() throws Exception {
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void insertPass() throws Exception {
        PersonDAO pDao = new PersonDAO(conn);
        pDao.insert(testPerson); // Insert a Person
        Person foundPerson = pDao.find(testPerson.getPersonID());
        assertNotNull(foundPerson, "Found Person was null");
        assertTrue(Person.isEqual(testPerson, foundPerson), "Found and inserted Persons do not match");
    }

    @Test
    public void insertFail() throws Exception {
        PersonDAO pDao = new PersonDAO(conn);
        pDao.insert(testPerson);
        assertThrows(DataAccessException.class, () -> {
            pDao.insert(testPerson);
        });
        db.closeConnection(false);
        conn = db.openConnection();

        // Make sure inserts were rolled back
        PersonDAO pDao2 = new PersonDAO(conn);
        pDao2 = new PersonDAO(conn);
        Person foundPerson = pDao2.find(testPerson.getPersonID());
        assertNull(foundPerson, "Found inserted Person that should have been rolled back.");
    }

    @Test
    public void findPass() throws Exception {
        PersonDAO pDao = new PersonDAO(conn);
        pDao.insert(testPerson); // Insert an Person
        Person foundPerson = pDao.find(testPerson.getPersonID());
        assertNotNull(foundPerson, "Found Person was null");
        assertTrue(Person.isEqual(testPerson, foundPerson), "Found and inserted Persons do not match");
    }

    @Test
    public void findFail() throws Exception {
        PersonDAO pDao = new PersonDAO(conn);
        pDao.insert(testPerson); // Insert an Person
        Person foundPerson = pDao.find(testPerson.getPersonID() + "extra_text");
        assertNull(foundPerson, "Found an Person when value should be null.");
    }

    @Test
    public void findAllPass() throws Exception {
        PersonDAO pDao = new PersonDAO(conn);
        for (int i = 0; i < 6; i++) {
            testPerson.setPersonID("Person_id" + i); // ID must be unique
            pDao.insert(testPerson);
        }

        ArrayList<Person> foundPersons = pDao.findAll(testPerson.getAssociatedUsername());
        assertTrue(foundPersons.size() > 0, "No Persons were found.");
        assertEquals(6, foundPersons.size(), "Didn't find exactly 6 Persons.");
    }

    @Test
    public void findAllPassTwo() throws Exception {
        PersonDAO pDao = new PersonDAO(conn);
        pDao.insert(testPerson);
        pDao.clear();
        ArrayList<Person> foundPersons = pDao.findAll(testPerson.getAssociatedUsername());
        assertEquals(0, foundPersons.size(), "Didn't find an Person array of size 0.");
    }

    @Test
    public void clearPass() throws Exception {
        PersonDAO pDao = new PersonDAO(conn);
        for (int i = 0; i < 100; i++) {
            testPerson.setPersonID("Person_id" + i); // ID must be unique
            pDao.insert(testPerson);
        }
        pDao.clear();
        ArrayList<Person> foundPersons = pDao.findAll(testPerson.getAssociatedUsername());
        assertEquals(0, foundPersons.size(), "Didn't find an Person array of size 0.");
    }

    @Test
    public void clearPassTwo() throws Exception {
        PersonDAO pDao = new PersonDAO(conn);
        pDao.insert(testPerson); // Insert an Person
        pDao.clear();
        Person foundPerson = pDao.find(testPerson.getPersonID());
        assertNull(foundPerson, "Found an Person when value should be null.");
        pDao.clear();
        ArrayList<Person> foundPersons = pDao.findAll(testPerson.getAssociatedUsername());
        assertEquals(0, foundPersons.size(), "Clearing an empty database caused problems.");
    }

    @Test
    public void deleteAllPass() throws Exception {
        PersonDAO pDao = new PersonDAO(conn);
        testPerson2.setAssociatedUsername("DifferentUsername");
        pDao.insert(testPerson);
        pDao.insert(testPerson2);

        pDao.deleteAll(testPerson.getAssociatedUsername());

        Person foundPerson = pDao.find(testPerson2.getPersonID());
        assertNotNull(foundPerson, "Could not find Person associated with different username.");
        foundPerson = pDao.find(testPerson.getAssociatedUsername());
        assertNull(foundPerson, "Found Person that should have been deleted.");
    }

    @Test
    public void deleteAllPassTwo() throws Exception {
        PersonDAO pDao = new PersonDAO(conn);
        testPerson2.setAssociatedUsername("DifferentUsername");

        for (int i = 0; i < 7; i++) {
            testPerson.setPersonID("Person" + i);
            testPerson2.setPersonID("differentPerson" + i);
            pDao.insert(testPerson);
            pDao.insert(testPerson2);
        }

        pDao.deleteAll("DifferentUsername");

        ArrayList<Person> foundPersons = pDao.findAll("DifferentUsername");
        assertEquals(0, foundPersons.size(), "Found Persons associated with deleted username.");
        foundPersons = pDao.findAll(testPerson.getAssociatedUsername());
        assertEquals(7, foundPersons.size(), "Didn't find 7 Persons belonging to the other user.");
    }
}
