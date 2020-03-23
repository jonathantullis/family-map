package _service;

import _dao.*;
import _model.AuthToken;
import _request.ClearRequest;
import _result.ClearResult;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClearService {
    private static Logger logger = Logger.getLogger("ClearService");

    /**
     * Makes requests to the Events, Users, Persons, and AuthToken DAOs to DELETE ALL data from the database,
     * including user accounts, auth tokens, and generated person and event data.
     *
     * @param r ClearRequest
     * @return ClearResult object containing boolean <code>success</code> and <code>message</code>
     */
    public ClearResult clear(ClearRequest r) {
        ClearResult result;
        Database db = new Database();
        AuthToken authToken;

        // Attempt to insert the new user
        try {
            Connection conn = db.openConnection();

            UserDAO uDao = new UserDAO(conn);
            PersonDAO pDao = new PersonDAO(conn);
            EventDAO eDao = new EventDAO(conn);
            AuthTokenDAO aDao = new AuthTokenDAO(conn);

            uDao.clear();
            pDao.clear();
            eDao.clear();
            aDao.clear();

            db.closeConnection(true);
        } catch(Exception e) {
            logger.log(Level.SEVERE, "Error clearing SQL tables", e);
            try {
                db.closeConnection(false);
            } catch(DataAccessException error) {
                logger.log(Level.SEVERE, "Error closing database connection.", error);
            }
            result = new ClearResult(false, "There was an error while clearing the database.");
            return result;
        }

        result = new ClearResult(true, "Clear succeeded!");

        return result;
    }
}
