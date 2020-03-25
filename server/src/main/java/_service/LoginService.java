package _service;

import _dao.AuthTokenDAO;
import _dao.DataAccessException;
import _dao.Database;
import _dao.UserDAO;
import _model.AuthToken;
import _model.User;
import _request.LoginRequest;
import _result.LoginResult;
import java.sql.Connection;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginService {
    private Logger logger = Logger.getLogger("LoginService");

    /**
     * Makes a request to the DAO to log in the user and returns an auth token.
     *
     * STEPS
     * 1. Make a request to the User DAO to check if the provided credentials are correct.
     * 2. If the request fails, generate a response body with the error message.
     * 3. If the request is successful, generate an AuthToken and make a request to the AuthToken
     *    DAO to insert it into the AuthToken table.
     *
     * @param r LoginRequest object.
     *
     * @return LoginResult object containing <code>authToken</code>, <code>username</code>, <code>personID</code>,
     * and boolean <code>success</code>
     */
    public LoginResult login(LoginRequest r) {
        LoginResult result;
        Database db = new Database();
        try {
            Connection conn = db.getConnection();
            UserDAO uDao = new UserDAO(conn);

            User user = uDao.find(r.getUserName());

            if (user != null && user.getPassword().equals(r.getPassword())) {
                AuthTokenDAO aDao = new AuthTokenDAO(conn);
                // Generate a new AuthToken and insert it into the database
                AuthToken authToken = new AuthToken(UUID.randomUUID().toString(), user.getUserName());
                aDao.insert(authToken);
                result = new LoginResult(authToken.getUserName(), authToken.getToken(), user.getPersonID());
                result.setMessage("Successfully Logged In");
            } else {
                result = new LoginResult("ERROR: Invalid username or password");
            }

            db.closeConnection(true);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Error accessing database", e);
            result = new LoginResult("Error accessing database");
            try {
                db.closeConnection(false);
            } catch(DataAccessException error) {
                logger.log(Level.SEVERE, "Could not close connection to database", error);
            }
        }
        return result;
    }
}
