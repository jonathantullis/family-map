package _service;

import _dao.Database;
import _dao.EventDAO;
import _model.Event;
import _request.AllEventsRequest;
import _result.AllEventsResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AllEventsServiceTest {
    private Database db;
    private Connection conn;
    private AllEventsService service;
    private Event testEvent;
    private Event testEvent2;
    private AllEventsRequest request;

    @BeforeEach
    public void setUp() {
        db = new Database();
        service = new AllEventsService();
        testEvent = new Event("eventID", "username", "personID",
                5.5, 6.4, "USA", "SLC", "nothingMuch",  2020);
        testEvent2 = new Event("differentID", "differentUsername", "personID",
                5.5, 6.4, "USA", "SLC", "nothingMuch",  2020);
    }

    @AfterEach
    public void tearDown() throws Exception {
        db.openConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void fetchPass() throws Exception {
        // Insert a bunch of Events into the DB
        conn = db.openConnection();
        db.clearTables();
        EventDAO pDao = new EventDAO(conn);
        // There should be 10 of each username. 20 total.
        for (int i = 0; i < 10; i++) {
            testEvent.setEventID("testID" + i);
            testEvent2.setEventID("otherTestID" + i);
            pDao.insert(testEvent);
            pDao.insert(testEvent2);
        }
        db.closeConnection(true);

        // Try and fetch the people associated with "username"
        request = new AllEventsRequest("username", "authToken");
        AllEventsResult result = service.fetch(request);

        assertNotNull(result, "Result was null");
        assertTrue(result.isSuccess(), "Response came back as failure.");
        assertEquals(10, result.getData().size(), "Data did not have exactly 10 Events.");
    }

    @Test
    public void fetchFail() throws Exception {
        // Insert a bunch of Events into the DB
        conn = db.openConnection();
        db.clearTables();
        EventDAO pDao = new EventDAO(conn);
        // There should be 10 of each username. 20 total.
        for (int i = 0; i < 10; i++) {
            testEvent.setEventID("testID" + i);
            testEvent2.setEventID("otherTestID" + i);
            pDao.insert(testEvent);
            pDao.insert(testEvent2);
        }
        db.closeConnection(true);

        // Try and fetch the people associated with "WRONG_USERNAME"
        request = new AllEventsRequest("WRONG_USERNAME", "authToken");
        AllEventsResult result = service.fetch(request);

        assertNotNull(result, "Result was null");
        assertFalse(result.isSuccess(), "Response should have been successful even though no data was found.");
        assertEquals("No events associated with the given username.", result.getMessage());
    }
}
