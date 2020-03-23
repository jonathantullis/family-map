package _dao;

import _model.AuthToken;

import java.sql.*;
import java.util.logging.Logger;

public class AuthTokenDAO {
    private static Logger logger = Logger.getLogger("AuthTokenDAO");
    private final Connection conn;

    public AuthTokenDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Insert authToken into User table in SQL database
     * @param authToken User object to be inserted into the SQL database
     * @throws DataAccessException if there are any SQL errors
     */
    public void insert(AuthToken authToken) throws DataAccessException {
        String sql = "INSERT INTO AuthTokens (Username, Token) VALUES(?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken.getUserName());
            stmt.setString(2, authToken.getToken());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.warning(e.getMessage());
            throw new DataAccessException("Error encountered while inserting AuthToken into the AuthToken table.");
        }
    }

    /**
     * Find user in the SQL database based on given username
     * @param token username of User to be found in the SQL database
     * @return found User object. Return null if not found.
     * @throws DataAccessException if there are any SQL errors
     */
    public AuthToken find(String token) throws DataAccessException {
        AuthToken authToken;
        ResultSet rs = null;
        String sql = "SELECT * FROM AuthTokens WHERE Token = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            rs = stmt.executeQuery();
            if (rs.next()) {
                authToken = new AuthToken(rs.getString("Token"), rs.getString("Username"));
                return authToken;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding AuthToken");
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
            stmt.executeUpdate("DELETE FROM AuthTokens");
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing the AuthToken table");
        }
    }
}
