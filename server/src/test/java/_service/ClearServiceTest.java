package _service;

import _dao.*;
import _model.AuthToken;
import _model.Event;
import _model.Person;
import _model.User;
import _request.ClearRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    private Database db;
    private Connection conn;
    private ClearService service;
    private ClearRequest request;
    private UserDAO uDao;
    private PersonDAO pDao;
    private EventDAO eDao;
    private AuthTokenDAO aDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();
        conn = db.openConnection();
        service = new ClearService();
        request = new ClearRequest();

        uDao = new UserDAO(conn);
        uDao.insert(new User("abc", "abc", "abc",
                "abc", "abc", "m", "abc"));

        pDao = new PersonDAO(conn);
        pDao.insert(new Person("abc", "abc", "abc",
                "abc", "f", null, null, null));

        eDao = new EventDAO(conn);
        eDao.insert(new Event("abc", "abc", "abc", 1.111, 2.222, "abc", "abc", "abc", 2020));

        aDao = new AuthTokenDAO(conn);
        aDao.insert(new AuthToken("abc", "abc"));

        try {
            conn.commit();
        } catch (SQLException e) {
            throw new DataAccessException();
        }
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void clearPass() throws DataAccessException {
        service.clear(request);

        // Check to make sure everything was deleted
        assertNull(uDao.find("abc"));
        assertNull(pDao.find("abc"));
        assertNull(eDao.find("abc"));
        assertNull(aDao.find("abc"));
    }

    @Test
    public void clearFail() {
        // FIXME what do I put in here?
    }
}
