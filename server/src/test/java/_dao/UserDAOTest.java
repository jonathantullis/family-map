package _dao;

import _model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

//We will use this to test that our insert method is working and failing in the right ways
public class UserDAOTest {
    private Database db = new Database();
    private Connection conn;
    private User testUser;

    @BeforeEach
    public void setUp() throws Exception {
        conn = db.openConnection();
        db.clearTables();
        testUser = new User("Kt123", "abcde4*", "katiecoolgal@gmail.com",
                "Katelyn", "Tullis", "f", "Jh123");
    }

    @AfterEach
    public void tearDown() throws Exception {
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void insertPass() throws Exception {
        UserDAO uDao = new UserDAO(conn);
        uDao.insert(testUser); // Insert an User
        User foundUser = uDao.find(testUser.getUserName());
        assertNotNull(foundUser, "Found User was null");
        assertTrue(User.isEqual(testUser, foundUser), "Found and inserted Users do not match");
    }

    @Test
    public void insertFail() throws Exception {
        UserDAO uDao = new UserDAO(conn);
        uDao.insert(testUser);
        assertThrows(DataAccessException.class, () -> {
            uDao.insert(testUser);
        });
        db.closeConnection(false);
        conn = db.openConnection();

        // Make sure inserts were rolled back
        UserDAO uDao2 = new UserDAO(conn);
        uDao2 = new UserDAO(conn);
        User foundUser = uDao2.find(testUser.getUserName());
        assertNull(foundUser, "Found inserted User that should have been rolled back.");
    }

    @Test
    public void findPass() throws Exception {
        UserDAO uDao = new UserDAO(conn);
        uDao.insert(testUser); // Insert an User
        User foundUser = uDao.find(testUser.getUserName());
        assertNotNull(foundUser, "Found User was null");
        assertTrue(User.isEqual(testUser, foundUser), "Found and inserted Users do not match");
    }
    
    @Test
    public void findFail() throws Exception {
        UserDAO uDao = new UserDAO(conn);
        uDao.insert(testUser); // Insert an User
        User foundUser = uDao.find(testUser.getUserName() + "extra_text");
        assertNull(foundUser, "Found an User when value should be null.");
        assertThrows(NullPointerException.class, () -> {
            uDao.insert(null);
        });
    }
    
    @Test
    public void clearPass() throws Exception {
        UserDAO uDao = new UserDAO(conn);
        for (int i = 0; i < 100; i++) {
            testUser.setUserName("UserName" + i); // userName must be unique
            uDao.insert(testUser);
        }
        uDao.clear();
        User foundUser = uDao.find("UserName0");
        assertNull(foundUser, "Found user that should have been cleared.");

        foundUser = uDao.find("UserName99");
        assertNull(foundUser, "Found user that should have been cleared.");
    }

    @Test
    public void clearPassTwo() throws Exception {
        UserDAO uDao = new UserDAO(conn);
        uDao.insert(testUser); // Insert an User
        uDao.clear();
        User foundUser = uDao.find(testUser.getUserName());
        assertNull(foundUser, "Found an User when value should be null.");
        uDao.clear();
    }
}
