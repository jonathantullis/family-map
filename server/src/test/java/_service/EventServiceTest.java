package _service;

import _dao.DataAccessException;
import _dao.Database;
import _dao.EventDAO;
import _model.Event;
import _request.EventRequest;
import _result.EventResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class EventServiceTest {
    private Database db;
    private Connection conn;
    private EventService service;
    private Event testEvent;
    private EventRequest request;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();
        service = new EventService();
        testEvent = new Event("eventID", "username", "jonny123",
                7.888, 1.234, "USA", "SLC", "Nothing much", 2020);
        request = new EventRequest(testEvent.getEventID(), testEvent.getAssociatedUsername(), "authToken");
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
        EventDAO eDao = new EventDAO(conn);
        eDao.insert(testEvent);
        db.closeConnection(true);


        // Try and fetch the person
        EventResult result = service.fetch(request);
        assertTrue(result.isSuccess(), "Response came back as failure.");

        // Compare the result and the original person
        assertEquals(testEvent.getEventID(), result.getEventID());
        assertEquals(testEvent.getAssociatedUsername(), result.getAssociatedUsername());
    }

    @Test
    public void fetchFail() throws Exception {
        // Insert person into database
        conn = db.openConnection();
        db.clearTables();
        EventDAO eDao = new EventDAO(conn);
        eDao.insert(testEvent);
        db.closeConnection(true);

        // Try and fetch the wrong person
        request.setEventID("WRONG");
        EventResult result = service.fetch(request);
        assertFalse(result.isSuccess(), "Request should have failed.");
    }
}
