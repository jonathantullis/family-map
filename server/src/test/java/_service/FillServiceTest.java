package _service;

import _dao.DataAccessException;
import _dao.Database;
import _request.FillRequest;
import _request.RegisterRequest;
import _result.FillResult;
import _result.RegisterResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;

public class FillServiceTest {
    private Database db = new Database();
    private Connection conn;
    private FillService service;

    @BeforeEach
    public void setUp() throws DataAccessException {
        service = new FillService();
        db.openConnection();
        db.clearTables();
        db.closeConnection(true);

        // User must be registered in order to user FillService
        RegisterService rs = new RegisterService();
        RegisterResult result = rs.register(new RegisterRequest("username", "password",
                "email", "fName", "lName", "m"));
        assertTrue(result.isSuccess(), "Failed to register user before fill tests.");
    }

    @AfterEach
    public void tearDown() throws Exception {
        db.openConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void fillPass() throws DataAccessException {
        // Try 3 generations
        FillRequest request = new FillRequest("username", 3);
        FillResult result = service.fill(request);
        assertTrue(result.isSuccess(), "Failed to execute fill for 3 generations.");
        assertEquals("Successfully added 15 persons and 43 events to the database.", result.getMessage());

        // Try ONLY filling the user.
        request.setGenerations(0);
        result = service.fill(request);
        assertTrue(result.isSuccess(), "Failed to execute fill for 0 generations.");
        assertEquals("Successfully added 1 persons and 1 events to the database.", result.getMessage());
    }

    @Test
    public void fillFail() {
        // Try invalid username
        FillRequest request = new FillRequest("INVALID_USERNAME", 3);
        FillResult result = service.fill(request);
        assertFalse(result.isSuccess(), "Should have failed with wrong username.");
        assertEquals("User does not exist", result.getMessage());

        // Try valid username and invalid generations
        request = new FillRequest("username", -3);
        result = service.fill(request);
        assertFalse(result.isSuccess(), "Should have failed with wrong username.");
        assertEquals("Invalid number of generations provided.", result.getMessage());
    }
}
