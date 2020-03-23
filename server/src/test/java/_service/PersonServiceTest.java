package _service;

import _dao.*;
import _model.Person;
import _request.PersonRequest;
import _result.PersonResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class PersonServiceTest {
    private Database db;
    private Connection conn;
    private PersonService service;
    private Person testPerson;
    private PersonRequest request;

    @BeforeEach
    public void setUp() {
        db = new Database();
        service = new PersonService();
        testPerson = new Person("personID", "username", "Jonny",
                "Tullis", "m", "id", "id", "id");
        request = new PersonRequest(testPerson.getPersonID(), testPerson.getAssociatedUsername(), "authToken");
    }

    @AfterEach
    public void tearDown() throws Exception {
        db.openConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void fetchPass() throws Exception {
        // Insert person into database
        conn = db.openConnection();
        db.clearTables();
        PersonDAO pDao = new PersonDAO(conn);
        pDao.insert(testPerson);
        db.closeConnection(true);


        // Try and fetch the person
        PersonResult result = service.fetch(request);
        assertTrue(result.isSuccess(), "Response came back as failure.");

        // Compare the result and the original person
        assertEquals(testPerson.getPersonID(), result.getPersonID());
        assertEquals(testPerson.getAssociatedUsername(), result.getAssociatedUsername());
    }

    @Test
    public void fetchFail() throws Exception {
        // Insert person into database
        conn = db.openConnection();
        db.clearTables();
        PersonDAO pDao = new PersonDAO(conn);
        pDao.insert(testPerson);
        db.closeConnection(true);

        // Try and fetch the wrong person
        request.setPersonID("WRONG");
        PersonResult result = service.fetch(request);
        assertFalse(result.isSuccess(), "Request should have failed.");
    }
}
