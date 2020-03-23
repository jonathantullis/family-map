package _service;

import _dao.DataAccessException;
import _dao.Database;
import _dao.EventDAO;
import _model.Event;
import _request.AllEventsRequest;
import _result.AllEventsResult;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AllEventsService {
    private static Logger logger = Logger.getLogger("GetAllEventsService");

    /**
     * Fetch ALL Events related to ALL Persons related to the user
     * 1. If the request fails, generate a response body with the error message.
     * 2. If the request is successful, make a request to the Event
     *    DAO to fetch all events associated with all relatives
     *    associated with the user's username.
     *
     * @param r AllEventsRequest with AuthToken
     * @return ALL Events related to ALL Persons related to the user
     */
    public AllEventsResult fetch(AllEventsRequest r) {
        Database db = new Database();
        Connection conn;
        AllEventsResult result;
        try {
            conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            ArrayList<Event> events = eDao.findAll(r.getUserName());
            result = new AllEventsResult(events);
            if (events.size() == 0) {
                result = new AllEventsResult("No events associated with the given username.");
            }
            db.closeConnection(true);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Error accessing database", e);
            result = new AllEventsResult("Internal server error");
            try {
                db.closeConnection(false);
            } catch (DataAccessException error) {
                logger.log(Level.SEVERE, "Error closing DB connection", error);
            }
        }
        return result;
    }
}
