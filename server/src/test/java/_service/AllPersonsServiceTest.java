package _service;

import _dao.Database;
import _dao.PersonDAO;
import _model.Person;
import _request.AllPersonsRequest;
import _result.AllPersonsResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class AllPersonsServiceTest {
    private Database db;
    private Connection conn;
    private AllPersonsService service;
    private Person testPerson;
    private Person testPerson2;
    private AllPersonsRequest request;

    @BeforeEach
    public void setUp() {
        db = new Database();
        service = new AllPersonsService();
        testPerson = new Person("personID", "username", "Jonny",
                "Tullis", "m", "id", "id", "id");
        testPerson2 = new Person("differentID", "differentUsername", "Katelyn",
                "Tullis", "f", "id", "id", "id");
    }

    @AfterEach
    public void tearDown() throws Exception {
        db.openConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void fetchPass() throws Exception {
        // Insert a bunch of persons into the DB
        conn = db.openConnection();
        db.clearTables();
        PersonDAO pDao = new PersonDAO(conn);
        // There should be 10 of each username. 20 total.
        for (int i = 0; i < 10; i++) {
            testPerson.setPersonID("testID" + i);
            testPerson2.setPersonID("otherTestID" + i);
            pDao.insert(testPerson);
            pDao.insert(testPerson2);
        }
        db.closeConnection(true);

        // Try and fetch the people associated with "username"
        request = new AllPersonsRequest("username", "authToken");
        AllPersonsResult result = service.fetch(request);

        assertNotNull(result, "Result was null");
        assertTrue(result.isSuccess(), "Response came back as failure.");
        assertEquals(10, result.getData().size(), "Data did not have exactly 10 Persons.");
    }

    @Test
    public void fetchFail() throws Exception {
        // Insert a bunch of persons into the DB
        conn = db.openConnection();
        db.clearTables();
        PersonDAO pDao = new PersonDAO(conn);
        // There should be 10 of each username. 20 total.
        for (int i = 0; i < 10; i++) {
            testPerson.setPersonID("testID" + i);
            testPerson2.setPersonID("otherTestID" + i);
            pDao.insert(testPerson);
            pDao.insert(testPerson2);
        }
        db.closeConnection(true);

        // Try and fetch the people associated with "WRONG_USERNAME"
        request = new AllPersonsRequest("WRONG_USERNAME", "authToken");
        AllPersonsResult result = service.fetch(request);

        assertNotNull(result, "Result was null");
        assertFalse(result.isSuccess(), "Response should have been successful even though no data was found.");
        assertEquals("No persons associated with the given username.", result.getMessage());
    }
}
