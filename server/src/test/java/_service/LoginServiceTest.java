package _service;

import _dao.AuthTokenDAO;
import _dao.DataAccessException;
import _dao.Database;
import _model.AuthToken;
import _request.LoginRequest;
import _request.RegisterRequest;
import _result.LoginResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {
    private Database db;
    private LoginService service;
    private LoginRequest request;
    private Connection conn;

    @BeforeEach
    public void setUp() throws DataAccessException {
        RegisterService registerService = new RegisterService();
        registerService.register(new RegisterRequest("Kt123", "abcde4*",
                "katiecoolgal@gmail.com", "Katelyn", "Tullis", "f"));

        db = new Database();
        conn = db.openConnection();
        service = new LoginService();
        request = new LoginRequest("Kt123", "abcde4*");
    }

    @AfterEach
    public void tearDown() throws Exception {
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void loginPass() throws DataAccessException {
        LoginResult result = service.login(request);

        // Login should have been successful
        assertNotNull(result);
        assertTrue(result.isSuccess());

        AuthToken foundToken = null;

        try {
            AuthTokenDAO aDao = new AuthTokenDAO(conn);
            foundToken = aDao.find(result.getAuthToken());
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }

        assertNotNull(foundToken);
        assertEquals(foundToken.getToken(), result.getAuthToken());
        assertEquals(foundToken.getUserName(), request.getUsername());
    }

    @Test
    public void loginFail() {

    }
}
