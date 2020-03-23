package _dao;

import _model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class PersonDAO {
    private static Logger logger = Logger.getLogger("PersonDAO");
    private final Connection conn;

    public PersonDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Insert Person into User table in SQL database
     * @param person Person object to be inserted into the SQL database
     * @throws DataAccessException if there are any SQL errors
     */
    public void insert(Person person) throws DataAccessException {
        String sql = "INSERT INTO Persons (PersonID, AssociatedUsername, FirstName, LastName, Gender, " +
                "FatherID, MotherID, SpouseID) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, person.getPersonID());
            stmt.setString(2, person.getAssociatedUsername());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4, person.getLastName());
            stmt.setString(5, person.getGender());
            stmt.setString(6, person.getFatherID());
            stmt.setString(7, person.getMotherID());
            stmt.setString(8, person.getSpouseID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.warning(e.getMessage());
            throw new DataAccessException("Error encountered while inserting person into the Persons table.");
        }
    }

    /**
     * Find person in the SQL database based on given PersonID
     * @param personID PersonID of Person to be found in the SQL databasse
     * @return found Person object. Return null if not found.
     * @throws DataAccessException if there are any SQL errors
     */
    public Person find(String personID) throws DataAccessException {
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE personID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                person = new Person(rs.getString("PersonID"), rs.getString("AssociatedUsername"),
                        rs.getString("FirstName"), rs.getString("LastName"), rs.getString("Gender"),
                        rs.getString("FatherID"), rs.getString("MotherID"), rs.getString("SpouseID"));

                return person;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding person");
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
     * @return ALL persons associated with the given username. Return null if none found.
     */
    public ArrayList<Person> findAll(String username) throws DataAccessException {
        ArrayList<Person> result = new ArrayList<>();
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE AssociatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(new Person(rs.getString("PersonID"), rs.getString("AssociatedUsername"),
                        rs.getString("FirstName"), rs.getString("LastName"), rs.getString("Gender"),
                        rs.getString("FatherID"), rs.getString("MotherID"), rs.getString("SpouseID")));
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding person");
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
     * Clear all contents of the Persons table in the SQL database
     * @throws DataAccessException if there are any SQL errors
     */
    public void clear() throws DataAccessException {
        try (Statement stmt = conn.createStatement()){
            stmt.executeUpdate("DELETE FROM Persons");
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing the Persons table");
        }
    }

    /**
     * Delete all persons associated with the given username
     * @param username username used to delete associated persons
     * @throws DataAccessException if there is a SQL error
     */
    public void deleteAll(String username) throws DataAccessException {
        String sql = "DELETE FROM Persons WHERE AssociatedUsername = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while deleting from the Persons table");
        }
    }
}
