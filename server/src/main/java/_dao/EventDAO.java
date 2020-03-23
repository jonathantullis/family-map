package _dao;

import _model.Event;
import _model.Person;

import java.sql.Types;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class EventDAO {
    private static Logger logger = Logger.getLogger("EventDAO");
    private final Connection conn;

    public EventDAO(Connection conn)
    {
        this.conn = conn;
    }

    /**
     * Insert event into User table in SQL database
     * @param event Event object to be inserted into the SQL database
     * @throws DataAccessException if there are any SQL errors
     */
    public void insert(Event event) throws DataAccessException {
        String sql = "INSERT INTO Events (EventID, AssociatedUsername, PersonID, Latitude, Longitude, " +
                "Country, City, EventType, Year) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getEventID());
            stmt.setString(2, event.getAssociatedUsername());
            stmt.setString(3, event.getPersonID());

            if (event.getLatitude() != null) {
                stmt.setDouble(4, event.getLatitude());
            } else {
                stmt.setNull(4, Types.DOUBLE);
            }

            if (event.getLongitude() != null) {
                stmt.setDouble(5, event.getLongitude());
            } else {
                stmt.setNull(5, Types.DOUBLE);
            }
            stmt.setString(6, event.getCountry());
            stmt.setString(7, event.getCity());
            stmt.setString(8, event.getEventType());

            if (event.getYear() != null) {
                stmt.setInt(9, event.getYear());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting event into the Events table.");
        }
    }

    /**
     * Find event in the SQL database based on given EventID
     * @param eventID EventID of event to be found in the SQL database
     * @return found Event object. Return null if not found.
     * @throws DataAccessException if there are any SQL errors
     */
    public Event find(String eventID) throws DataAccessException {
        Event event;
        ResultSet rs = null;
        String sql = "SELECT * FROM Events WHERE EventID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                event = new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getDouble("Latitude"), rs.getDouble("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));

                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    public Event find(String personID, String eventType) throws DataAccessException {
        ResultSet rs = null;
        String sql = "SELECT * FROM Events WHERE PersonID = ? AND EventType = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personID);
            stmt.setString(2, eventType);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getDouble("Latitude"), rs.getDouble("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

    /**
     * @param username username of user
     * @return ALL events associated with ALL persons associated with the given username. Return null if none found.
     */
    public ArrayList<Event> findAll(String username) throws DataAccessException {
        ArrayList<Event> events = new ArrayList<>();
        ResultSet rs = null;
        String sql = "SELECT * FROM Events WHERE AssociatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getDouble("Latitude"), rs.getDouble("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year")));
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * Clear all contents of the Events table in the SQL database
     * @throws DataAccessException if there are any SQL errors
     */
    public void clear() throws DataAccessException {
        try (Statement stmt = conn.createStatement()){
            stmt.executeUpdate("DELETE FROM Events");
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing the Events table");
        }
    }

    /**
     * Delete all events associated with the given username
     * @param username username used to delete associated events
     * @throws DataAccessException if there is a SQL error
     */
    public void deleteAll(String username) throws DataAccessException {
        String sql = "DELETE FROM Events WHERE AssociatedUsername = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while deleting from the Events table");
        }
    }
}
