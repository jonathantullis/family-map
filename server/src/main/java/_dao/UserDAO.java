package _dao;

import _model.User;

import java.sql.*;
import java.util.logging.Logger;

public class UserDAO {
    private static Logger logger = Logger.getLogger("UserDAO");

    private final Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Insert user into User table in SQL database
     * @param user User object to be inserted into the SQL database
     * @throws DataAccessException if there are any SQL errors
     */
    public void insert(User user) throws DataAccessException {
        String sql = "INSERT INTO Users (Username, Password, Email, FirstName, LastName, Gender, PersonID) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setString(6, user.getGender());
            stmt.setString(7, user.getPersonID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting user into the Users table");
        }
    }

    /**
     * Find user in the SQL database based on given username
     * @param username username of User to be found in the SQL database
     * @return found User object. Return null if not found.
     * @throws DataAccessException if there are any SQL errors
     */
    public User find(String username) throws DataAccessException {
        User user;
        ResultSet rs = null;
        String sql = "SELECT * FROM Users WHERE Username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getString("Username"), rs.getString("Password"), rs.getString("Email"),
                        rs.getString("FirstName"), rs.getString("LastName"), rs.getString("Gender"),
                        rs.getString("PersonID"));

                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding user");
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
     * Clear all contents of the Users table in the SQL database
     * @throws DataAccessException if there are any SQL errors
     */
    public void clear() throws DataAccessException {
        try (Statement stmt = conn.createStatement()){
            stmt.executeUpdate("DELETE FROM Users");
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing the Users table");
        }
    }
}
