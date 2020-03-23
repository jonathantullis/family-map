package _service;

import _dao.AuthTokenDAO;
import _dao.DataAccessException;
import _dao.Database;
import _dao.UserDAO;
import _model.AuthToken;
import _request.FillRequest;
import _request.RegisterRequest;
import _result.FillResult;
import _result.RegisterResult;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.UUID;

import java.sql.Connection;

public class RegisterService {
    private static Logger logger = Logger.getLogger("RegisterService");

    /**
     * Makes a request to the proper DAOs to create a new user account, generates 4 generations of ancestor data for the new
     * user, logs the user in, and returns an auth token.
     *
     * STEPS
     * 1. Make a request to the User DAO to attempt insertion of the user data into the User SQL table.
     * 2. If the request fails, generate a response body with an error message for the user.
     * 3. If the request is successful, generate an AuthToken model object and make a request to the AuthToken
     *      DAO to insert it into the AuthToken table.
     * 4. Generate a response body with the AuthToken and return it.
     *
     * @param r RegisterRequest object containing <code>username</code>, <code>password</code>,
     *          <code>email</code>, <code>firstname</code>, <code>lastname</code>, and <code>gender</code>.
     *
     * @return RegisterResult object containing <code>authToken</code>, <code>username</code>, <code>personID</code>,
     *      * and boolean <code>success</code>
     */
    public RegisterResult register(RegisterRequest r) {
        RegisterResult result;
        Database db = new Database();
        AuthToken authToken;

        // Attempt to insert the new user
        try {
            Connection conn = db.openConnection();
            // Insert the user
            UserDAO uDao = new UserDAO(conn);
            uDao.insert(r.getUser());
            // Generate and insert new AuthToken for user
            AuthTokenDAO aDao = new AuthTokenDAO(conn);
            authToken = new AuthToken(UUID.randomUUID().toString(), r.getUser().getUserName());
            aDao.insert(authToken);
            db.closeConnection(true);
        } catch(Exception e) {
            try {
                db.closeConnection(false);
            } catch(DataAccessException error) {
                logger.log(Level.SEVERE, "Error closing database connection", error);
            }
            result = new RegisterResult("ERROR: Registration failed");
            return result;
        }

        // User FillService to generate ancestor data
        FillService fillService = new FillService();
        FillResult fillResult = fillService.fill(new FillRequest(r.getUser().getUserName()));
        if (!fillResult.isSuccess()) {
            logger.severe("Error generating ancestor data during user registration");
        }
        result = new RegisterResult(authToken.getUserName(), authToken.getToken(), r.getUser().getPersonID());
        return result;
    }
}
