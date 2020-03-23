package _service;

import _dao.DataAccessException;
import _dao.Database;
import _dao.EventDAO;
import _model.Event;
import _request.EventRequest;
import _result.EventResult;
import _result.PersonResult;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventService {
    private static Logger logger = Logger.getLogger("GetEventService");
    /**
     * Fetch an event from the database
     * 1. Make a request to the AuthToken DAO to check if the provided credentials are correct.
     * 2. If the request fails, generate a response body with the error message.
     * 3. If the request is successful, make a request to the Event
     *    DAO to fetch the event associated with the given event ID.
     *
     * @param r EventRequest with AuthToken and EventID
     * @return Event object model of fetched event
     */
    public EventResult fetch(EventRequest r) {
        Database db = new Database();
        Connection conn;
        EventResult result;
        try {
            conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            Event e = eDao.find(r.getEventID());
            if (e != null) {
                result = new EventResult(e.getEventID(), e.getAssociatedUsername(), e.getPersonID(), e.getLatitude(),
                        e.getLongitude(), e.getCountry(), e.getCity(), e.getEventType(), e.getYear());
            } else {
                result = new EventResult("Error: Event not found.");
            }
            db.closeConnection(true);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Error accessing database", e);
            result = new EventResult("Internal server error");
            try {
                db.closeConnection(false);
            } catch (DataAccessException error) {
                logger.log(Level.SEVERE, "Error closing DB connection", error);
            }
        }
        return result;
    }
}
