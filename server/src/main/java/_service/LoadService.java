package _service;

import _dao.*;
import _model.Event;
import _model.Person;
import _model.User;
import _request.LoadRequest;
import _result.LoadResult;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadService {
    private static Logger logger = Logger.getLogger("LoadService");
    /**
     * Clears all data from the database (just like the ClearService), and then loads the
     * posted users, persons, and events data into the database.
     *
     * The “users” property in the request body contains an array of users to be
     * created. The “persons” and “events” properties contain family history information for these
     * users. The objects contained in the “persons” and “events” arrays should be added to the
     * server’s database.
     *
     * @param r LoadRequest object containing <code>Users</code>, <code>Persons</code>, and <code>Events</code>
     * @return LoadResult indicating either success or failure.
     */
    public LoadResult load(LoadRequest r) {
        Database db = new Database();

        // Insert all users, events, and persons
        try {
            Connection conn = db.openConnection();
            db.clearTables();
            UserDAO uDao = new UserDAO(conn);
            for (User user : r.getUsers()) {
                uDao.insert(user);
            }
            PersonDAO pDao = new PersonDAO(conn);
            for (Person person : r.getPersons()) {
                pDao.insert(person);
            }
            EventDAO eDao = new EventDAO(conn);
            for (Event event : r.getEvents()) {
                eDao.insert(event);
            }
            db.closeConnection(true);
        } catch(Exception e) {
            logger.warning("Error loading data");
            try {
                db.closeConnection(false);
            } catch(DataAccessException error) {
                logger.log(Level.SEVERE, "Error closing database connection", error);
            }
            return new LoadResult(false, "Error loading data");
        }

        return new LoadResult(true, "Successfully added " + r.getUsers().size() + " users, " +
                r.getPersons().size() + " persons, and " + r.getEvents().size() + " events to the database.");
    }
}
