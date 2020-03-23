package _handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultHandler implements HttpHandler {
    private static Logger logger = Logger.getLogger("DefaultHandler");

    @Override
    public void handle (HttpExchange exchange) throws IOException {
        try {
            String respData;
            // Stream 404 file into string
            String urlPath = exchange.getRequestURI().toString();
            if (urlPath == null || urlPath.equals("/")) {
                urlPath = "/index.html";
            }
            String filePath = "server/web" + urlPath;
            File file = new File(filePath);
            if (file.exists()) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            } else {
                file = new File("server/web/HTML/404.html");
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
            }
            // Read entire contents of file
            respData = new Scanner(file).useDelimiter("\\A").next();
            OutputStream respBody = exchange.getResponseBody();
            Handler.writeString(respData, respBody);
            respBody.close();
        } catch (Exception e) {
            // Some kind of internal error has occurred inside the server.
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            exchange.getResponseBody().close();
            logger.log(Level.SEVERE, "Internal server error", e);
        }
    }
}
