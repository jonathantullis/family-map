package _service;

import _dao.DataAccessException;
import _dao.Database;
import _dao.PersonDAO;
import _model.Person;
import _request.AllPersonsRequest;
import _result.AllPersonsResult;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AllPersonsService {
    private static Logger logger = Logger.getLogger("GetAllPersonsService");
    /**
     * Fetch ALL persons related to the user
     * 1. Make a request to the AuthToken DAO to check if the provided credentials are correct.
     * 2. If the request fails, generate a response body with the error message.
     * 3. If the request is successful, make a request to the Person
     *    DAO to fetch all persons associated with the user's username.
     *
     * @param r AllPersonRequest with AuthToken
     * @return ALL Persons related to the user
     */
    public AllPersonsResult fetch(AllPersonsRequest r) {
        Database db = new Database();
        Connection conn;
        AllPersonsResult result;
        try {
            conn = db.openConnection();
            PersonDAO pDao = new PersonDAO(conn);
            ArrayList<Person> persons = pDao.findAll(r.getUserName());
            result = new AllPersonsResult(persons);
            if (persons.size() == 0) {
                result = new AllPersonsResult("No persons associated with the given username.");
            }
            db.closeConnection(true);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Error accessing database", e);
            result = new AllPersonsResult("Internal server error");
            try {
                db.closeConnection(false);
            } catch (DataAccessException error) {
                logger.log(Level.SEVERE, "Error closing DB connection", error);
            }
        }
        return result;
    }
}
