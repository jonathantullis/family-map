package _service;

import _dao.*;
import _model.Event;
import _model.Person;
import _model.User;
import _request.LoadRequest;
import _result.LoadResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class LoadServiceTest {
    private Database db = new Database();
    private Connection conn;
    private LoadService service;
    private ArrayList<User> users;
    private ArrayList<Person> persons;
    private ArrayList<Event> events;

    @BeforeEach
    public void setUp() throws DataAccessException {
        users = getRandomUsers();
        persons = getRandomPersons();
        events = getRandomEvents();
        service = new LoadService();
        db.openConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @AfterEach
    public void tearDown() throws Exception {
        db.openConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    private ArrayList<User> getRandomUsers() {
        ArrayList<User> result = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            result.add(new User("username" + i, "password", "email",
                    "fName", "lName", "m", "personID" + i));
        }
        return result;
    }

    private ArrayList<Event> getRandomEvents() {
        ArrayList<Event> result = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            result.add(new Event("eventID" + i, "username0", "personID" + i + i,
                    6.5, 1.2, "USA", "SLC", "NothingMuch", 2020));
        }
        return result;
    }

    private ArrayList<Person> getRandomPersons() {
        ArrayList<Person> result = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            result.add(new Person("personID" + i + i, "username0", "fName",
                    "lName", "m", "id", "id", "id"));
        }
        return result;
    }

    @Test
    public void loadPass() throws DataAccessException {
        LoadRequest request = new LoadRequest(users, persons, events);
        LoadResult result = service.load(request);
        assertTrue(result.isSuccess(), "Load was not successful.");

        conn = db.openConnection();
        PersonDAO pDao = new PersonDAO(conn);
        UserDAO uDao = new UserDAO(conn);
        EventDAO eDao = new EventDAO(conn);

        ArrayList<Person> personsFound = pDao.findAll("username0");
        assertEquals(persons.size(), personsFound.size(), "Loaded Persons and Persons found in DB do not match.");

        ArrayList<Event> eventsFound = eDao.findAll("username0");
        assertEquals(events.size(), eventsFound.size(), "Loaded Events and Events found in DB do not match.");

        for (User user : users) {
            User foundUser = uDao.find(user.getUserName());
            assertNotNull(foundUser, "Loaded Users and Users found in DB do not match.");
        }

        db.closeConnection(false);
    }

    @Test
    public void loadFail() throws DataAccessException {
        // Change username for each user to match so that there will be a UNIQUE error in the SQL database
        for (User user : users) {
            user.setUserName("SAME_USERNAME");
        }

        LoadRequest request = new LoadRequest(users, persons, events);
        LoadResult result = service.load(request);
        assertFalse(result.isSuccess(), "Load was successful when it should have failed.");

        conn = db.openConnection();
        PersonDAO pDao = new PersonDAO(conn);
        UserDAO uDao = new UserDAO(conn);
        EventDAO eDao = new EventDAO(conn);

        ArrayList<Person> personsFound = pDao.findAll("username0");
        assertEquals(0, personsFound.size(), "No Persons should exist in DB.");

        ArrayList<Event> eventsFound = eDao.findAll("username0");
        assertEquals(0, eventsFound.size(), "No Events should exist in DB.");

        for (User user : users) {
            User foundUser = uDao.find(user.getUserName());
            assertNull(foundUser, "Found user where none should exist.");
        }

        db.closeConnection(false);
    }
}
