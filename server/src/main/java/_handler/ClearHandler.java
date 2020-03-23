package _handler;

import _request.ClearRequest;
import _result.ClearResult;
import _service.ClearService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClearHandler implements Handler {
    private static Logger logger = Logger.getLogger("ClearHandler");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                ClearService clearService = new ClearService();
                ClearResult result = clearService.clear(new ClearRequest());

                // Parse result object into json string
                Gson gson = new Gson();
                String json = gson.toJson(result);
                OutputStream respBody = exchange.getResponseBody();
                Handler.writeString(json, respBody);

                respBody.close();
            } else {
                // Expecting a POST but got something else
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        } catch (Exception e) {
            // Some kind of internal error has occurred inside the server.
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            exchange.getResponseBody().close();
            logger.log(Level.SEVERE, "Internal server error", e);
        }
    }
}
