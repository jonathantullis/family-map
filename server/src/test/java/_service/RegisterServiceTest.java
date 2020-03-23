package _service;

import _dao.AuthTokenDAO;
import _dao.Database;
import _dao.UserDAO;
import _model.AuthToken;
import _model.User;
import _request.RegisterRequest;
import _result.RegisterResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTest {
    private Database db = new Database();
    private Connection conn;
    private RegisterService service;
    private RegisterRequest request;

    @BeforeEach
    public void setUp() {
        service = new RegisterService();
        request = new RegisterRequest("Kt123", "abcde4*", "katiecoolgal@gmail.com",
                "Katelyn", "Tullis", "f");
    }

    @AfterEach
    public void tearDown() throws Exception {
        db.openConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void registerPass() throws Exception {
        // Try and register the user
        RegisterResult result = service.register(request);
        assertTrue(result.isSuccess(), "Response came back as failure.");

        // Look for user in the database
        conn = db.openConnection();
        UserDAO uDao = new UserDAO(conn);
        User foundUser = uDao.find(result.getUserName());
        assertNotNull(foundUser, "Could not find user in DB after registration.");
        assertTrue(User.isEqual(foundUser, request.getUser()));
        db.closeConnection(false);

        // Look for the AuthToken in the AuthTokens SQL table
        conn = db.openConnection();
        AuthTokenDAO aDao = new AuthTokenDAO(conn);
        AuthToken foundToken = aDao.find(result.getAuthToken());
        assertNotNull(foundToken, "Could not find authToken in DB after registration.");
        db.closeConnection(false);
    }

    @Test
    public void registerFail() throws Exception {
        // Try and register the user
        RegisterResult successResult = service.register(request);
        assertTrue(successResult.isSuccess(), "Response came back as failure.");

        RegisterResult failedResult = service.register(request); // Cannot register same userName twice
        assertFalse(failedResult.isSuccess(), "Response came back as success.");

        // First user should still be in the database
        conn = db.openConnection();
        UserDAO uDao = new UserDAO(conn);
        User foundUser = uDao.find(successResult.getUserName());
        assertNotNull(foundUser, "Could not find user in DB after registration.");
        assertTrue(User.isEqual(foundUser, request.getUser()));
        db.closeConnection(false);

        // Look for the AuthToken in the AuthTokens SQL table
        conn = db.openConnection();
        AuthTokenDAO aDao = new AuthTokenDAO(conn);
        AuthToken foundToken = aDao.find(successResult.getAuthToken());
        assertNotNull(foundToken, "Could not find authToken in DB after registration.");
        db.closeConnection(false);
    }
}
