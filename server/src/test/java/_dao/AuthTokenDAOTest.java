package _dao;

import _model.AuthToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTokenDAOTest {
    private Database db;
    private AuthToken testToken;
    private AuthToken testToken2;

    @BeforeEach
    public void setUp() throws Exception {
        db = new Database();
        testToken = new AuthToken("authToken1234*", "myUsername");
        testToken2 = new AuthToken("afkjlvjie", "otherUsername");
    }

    @AfterEach
    public void tearDown() throws Exception {
        db.openConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void insertPass() throws Exception {
        AuthToken compareTest = null;

        try {
            Connection conn = db.openConnection();
            AuthTokenDAO aDao = new AuthTokenDAO(conn);
            aDao.insert(testToken);
            compareTest = aDao.find(testToken.getToken());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        assertNotNull(compareTest);
        assertTrue(AuthToken.isEqual(testToken, compareTest));
    }

    @Test
    public void insertFail() throws Exception {
        try {
            Connection conn = db.openConnection();
            AuthTokenDAO pDao = new AuthTokenDAO(conn);
            pDao.insert(testToken);
            assertThrows(DataAccessException.class, () -> {
                pDao.insert(testToken);
            });
            db.closeConnection(false);
        } catch (DataAccessException e) {
            System.out.println("Error accessing data");
            throw e;
        }

        //Both instances of insert should have been rolled back. Check that.
        AuthToken compareTest = testToken;
        try {
            Connection conn = db.openConnection();
            AuthTokenDAO aDao = new AuthTokenDAO(conn);

            compareTest = aDao.find(testToken.getToken());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }

        assertNull(compareTest);
    }

    @Test
    public void findPass() throws Exception {
        AuthToken foundToken = null;

        try {
            Connection conn = db.openConnection();
            AuthTokenDAO aDao = new AuthTokenDAO(conn);

            aDao.insert(testToken);

            foundToken = aDao.find(testToken.getToken());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        assertNotNull(foundToken);
        assertTrue(AuthToken.isEqual(testToken, foundToken));
    }

    @Test
    public void findFail() throws Exception {
        AuthToken compareTest = null;
        try {
            Connection conn = db.openConnection();
            AuthTokenDAO aDao = new AuthTokenDAO(conn);
            // Insert
            aDao.insert(testToken);
            compareTest = aDao.find("wrongAuthtoken");
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }

        assertNull(compareTest);
    }

    @Test
    public void clearPass() throws Exception {
        AuthToken result = null;
        try {
            Connection conn = db.openConnection();
            AuthTokenDAO aDao = new AuthTokenDAO(conn);
            aDao.insert(testToken);
            aDao.clear();
            result = aDao.find(testToken.getToken());
            assertNull(result, "Deleted token was found in database.");
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }

        assertNull(result);
    }

    @Test
    public void clearPassTwo() throws Exception {
        AuthToken result = null;
        try {
            Connection conn = db.openConnection();
            AuthTokenDAO aDao = new AuthTokenDAO(conn);
            aDao.insert(testToken);
            aDao.insert(testToken2);

            aDao.clear();
            result = aDao.find(testToken.getToken());
            assertNull(result, "Found first token in DB.");
            result = aDao.find(testToken2.getToken());
            assertNull(result, "Found second token in DB.");

            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
    }
}
