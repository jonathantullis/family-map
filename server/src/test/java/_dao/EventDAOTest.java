package _dao;

import _model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class EventDAOTest {
    private Database db = new Database();
    private Connection conn;
    private Event testEvent;
    private Event testEvent2;

    @BeforeEach
    public void setUp() throws Exception {
        conn = db.openConnection();
        db.clearTables();
        testEvent = new Event("Biking_123A", "Gale", "Gale123A",
                10.3, 10.3, "Japan", "Ushiku",
                "Biking_Around", 2016);
        testEvent2 = new Event("Biking_123B", "Gale", "Gale123A",
                10.3, 10.3, "Japan", "Ushiku",
                "Biking_Around", 2016);
    }

    @AfterEach
    public void tearDown() throws Exception {
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void insertPass() throws Exception {
        EventDAO eDao = new EventDAO(conn);
        eDao.insert(testEvent); // Insert an event
        Event foundEvent = eDao.find(testEvent.getEventID());
        assertNotNull(foundEvent, "Found event was null");
        assertTrue(Event.isEqual(testEvent, foundEvent), "Found and inserted events do not match");
    }

    @Test
    public void insertFail() throws Exception {
        EventDAO eDao = new EventDAO(conn);
        eDao.insert(testEvent);
        assertThrows(DataAccessException.class, () -> {
            eDao.insert(testEvent);
        });
        db.closeConnection(false);
        conn = db.openConnection();

        // Make sure inserts were rolled back
        EventDAO eDao2 = new EventDAO(conn);
        eDao2 = new EventDAO(conn);
        Event foundEvent = eDao2.find(testEvent.getEventID());
        assertNull(foundEvent, "Found inserted event that should have been rolled back.");
    }

    @Test
    public void findPass() throws Exception {
        EventDAO eDao = new EventDAO(conn);
        eDao.insert(testEvent); // Insert an event
        Event foundEvent = eDao.find(testEvent.getEventID()); // ONE param
        assertNotNull(foundEvent, "Found event was null");
        assertTrue(Event.isEqual(testEvent, foundEvent), "Found and inserted events do not match");
    }

    @Test
    public void findTwoPass() throws Exception {
        EventDAO eDao = new EventDAO(conn);
        eDao.insert(testEvent);
        Event foundEvent = eDao.find(testEvent.getPersonID(), testEvent.getEventType()); // TWO params
        assertNotNull(foundEvent, "Found event was null");
        assertTrue(Event.isEqual(testEvent, foundEvent), "Found and inserted events do not match");
    }

    @Test
    public void findFail() throws Exception {
        EventDAO eDao = new EventDAO(conn);
        eDao.insert(testEvent); // Insert an event
        Event foundEvent = eDao.find(testEvent.getEventID() + "extra_text"); // ONE param
        assertNull(foundEvent, "Found an event when value should be null.");
    }

    @Test
    public void findTwoFail() throws Exception {
        EventDAO eDao = new EventDAO(conn);
        eDao.insert(testEvent);
        Event foundEvent = eDao.find(testEvent.getPersonID() + "extra_text", testEvent.getEventType()); // TWO params
        assertNull(foundEvent, "Found an event when value should be null.");
    }

    @Test
    public void findAllPass() throws Exception {
        EventDAO eDao = new EventDAO(conn);
        for (int i = 0; i < 6; i++) {
            testEvent.setEventID("event_id" + i); // ID must be unique
            testEvent.setEventType("Running " + i + " miles."); // Change this just for fun too
            eDao.insert(testEvent);
        }

        ArrayList<Event> foundEvents = eDao.findAll(testEvent.getAssociatedUsername());
        assertTrue(foundEvents.size() > 0, "No events were found.");
        assertEquals(6, foundEvents.size(), "Didn't find exactly 6 events.");
    }

    @Test
    public void findAllPassTwo() throws Exception {
        EventDAO eDao = new EventDAO(conn);
        eDao.insert(testEvent);
        eDao.clear();
        ArrayList<Event> foundEvents = eDao.findAll(testEvent.getAssociatedUsername());
        assertEquals(0, foundEvents.size(), "Didn't find an Event array of size 0.");
    }

    @Test
    public void clearPass() throws Exception {
        EventDAO eDao = new EventDAO(conn);
        for (int i = 0; i < 100; i++) {
            testEvent.setEventID("event_id" + i); // ID must be unique
            eDao.insert(testEvent);
        }
        eDao.clear();
        ArrayList<Event> foundEvents = eDao.findAll(testEvent.getAssociatedUsername());
        assertEquals(0, foundEvents.size(), "Didn't find an Event array of size 0.");
    }

    @Test
    public void clearPassTwo() throws Exception {
        EventDAO eDao = new EventDAO(conn);
        eDao.insert(testEvent); // Insert an event
        eDao.clear();
        Event foundEvent = eDao.find(testEvent.getEventID());
        assertNull(foundEvent, "Found an event when value should be null.");
        eDao.clear();
        ArrayList<Event> foundEvents = eDao.findAll(testEvent.getAssociatedUsername());
        assertEquals(0, foundEvents.size(), "Clearing an empty database caused problems.");
    }

    @Test
    public void deleteAllPass() throws Exception {
        EventDAO eDao = new EventDAO(conn);
        testEvent2.setAssociatedUsername("DifferentUsername");
        eDao.insert(testEvent);
        eDao.insert(testEvent2);

        eDao.deleteAll(testEvent.getAssociatedUsername());

        Event foundEvent = eDao.find(testEvent2.getEventID());
        assertNotNull(foundEvent, "Could not find event associated with different username.");
        foundEvent = eDao.find(testEvent.getAssociatedUsername());
        assertNull(foundEvent, "Found event that should have been deleted.");
    }

    @Test
    public void deleteAllPassTwo() throws Exception {
        EventDAO eDao = new EventDAO(conn);
        testEvent2.setAssociatedUsername("DifferentUsername");

        for (int i = 0; i < 7; i++) {
            testEvent.setEventID("event" + i);
            testEvent2.setEventID("differentEvent" + i);
            eDao.insert(testEvent);
            eDao.insert(testEvent2);
        }

        eDao.deleteAll("DifferentUsername");

        ArrayList<Event> foundEvents = eDao.findAll("DifferentUsername");
        assertEquals(0, foundEvents.size(), "Found events associated with deleted username.");
        foundEvents = eDao.findAll(testEvent.getAssociatedUsername());
        assertEquals(7, foundEvents.size(), "Didn't find 7 events belonging to the other user.");
    }
}
