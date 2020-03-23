package _service;

import _dao.*;
import _model.Person;
import _request.PersonRequest;
import _result.PersonResult;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersonService {
    private static Logger logger = Logger.getLogger("GetPersonsService");

    /**
     * Fetch a person from the database
     * 1. If the request fails, generate a response body with the error message.
     * 2. If the request is successful, make a request to the Person
     *    DAO to fetch the person associated with the given person ID.
     *
     * @param r PersonRequest with AuthToken and PersonID
     * @return Person object model of fetched person
     */
    public PersonResult fetch(PersonRequest r) {
        PersonResult result;
        Database db = new Database();
        Connection conn;
        try {
            conn = db.openConnection();
            PersonDAO pDao = new PersonDAO(conn);
            Person p = pDao.find(r.getPersonID());
            if (p != null) {
                result = new PersonResult(p.getPersonID(), p.getAssociatedUsername(), p.getFirstName(),
                        p.getLastName(), p.getGender(), p.getFatherID(), p.getMotherID(), p.getSpouseID());
            } else {
                result = new PersonResult("Error: Person not found.");
            }
            db.closeConnection(true);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error accessing database", e);
            result = new PersonResult("Internal server error");
            try {
                db.closeConnection(false);
            } catch (DataAccessException error) {
                logger.log(Level.SEVERE, "Error closing DB connection", error);
            }
        }
        return result;
    }
}
