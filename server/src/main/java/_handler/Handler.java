package _handler;

import _dao.AuthTokenDAO;
import _dao.DataAccessException;
import _dao.Database;
import _model.AuthToken;
import _model.User;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.sql.Connection;
import java.util.logging.Logger;

public interface Handler extends HttpHandler {
    Logger logger = Logger.getLogger("HttpHandler");

    /**
     * Read a String from an InputStream.
     * @param is input stream to read from
     * @return string extracted from input stream
     * @throws IOException if there is an IO error
     */
    static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    /**
     * Write a string to the provided OutputStream
     * @param str String
     * @param os OutputStream to be written
     * @throws IOException if error
     */
    static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(sw);
        bw.write(str);
        bw.flush();
    }

    static User getUserFromRequestBody(InputStream body) throws IOException {
        Gson gson = new Gson();
        String json = Handler.readString(body);
        return gson.fromJson(json, User.class);
    }

    static boolean validAuthToken(String token, String requestUsername) throws DataAccessException {
        assert (requestUsername != null);
        boolean validAuthToken = false;
        Database db = new Database();
        try {
            Connection conn = db.getConnection();
            AuthTokenDAO aDao = new AuthTokenDAO(conn);
            AuthToken authToken = aDao.find(token);
            if (authToken != null) {
                if (authToken.getUserName().equals(requestUsername)) {
                    validAuthToken = true;
                }
            }
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
            logger.severe("Error verifying provided authToken");
        }

        return validAuthToken;
    }

    static boolean authTokenExists(String token) throws DataAccessException {
        boolean exists = false;
        Database db = new Database();
        try {
            Connection conn = db.getConnection();
            AuthTokenDAO aDao = new AuthTokenDAO(conn);
            AuthToken authToken = aDao.find(token);
            if (authToken != null) {
                exists = true;
            }
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
            logger.severe("Error verifying provided authToken");
        }

        return exists;
    }

    static AuthToken getAuthToken(String token) throws DataAccessException {
        Database db = new Database();
        AuthToken result = null;
        try {
            Connection conn = db.getConnection();
            AuthTokenDAO aDao = new AuthTokenDAO(conn);
            result = aDao.find(token);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
            logger.severe("Error verifying provided authToken");
        }

        return result;
    }

    static String getSecondIdentifier(URI uri) {
        String id = null;
        String path = uri.getPath() + "/";
        String[] data = path.split("/");

        if (data.length == 3) {
            id = data[2];
        }

        return id;
    }
}
