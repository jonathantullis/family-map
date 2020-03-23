package _handler;

import _request.FillRequest;
import _result.FillResult;
import _service.FillService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.logging.Level;

public class FillHandler implements Handler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {
                // Start sending the HTTP response to the client
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                // Parse the request and create fillRequest
                FillRequest request = getFillRequest(exchange.getRequestURI());
                if (request != null) {
                    // Call the fillService and generate family data for user
                    FillService service = new FillService();
                    FillResult result = service.fill(request);

                    // Parse result object into json string
                    Gson gson = new Gson();
                    String json = gson.toJson(result);
                    OutputStream respBody = exchange.getResponseBody();
                    Handler.writeString(json, respBody);

                    // Close the output stream
                    respBody.close();
                } else {
                    // Arguments invalid
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    exchange.getResponseBody().close();
                }
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

    private FillRequest getFillRequest(URI uri) {
        int dataStart = "/fill/".length();
        String path = uri.getPath();
        String[] data = path.substring(dataStart).split("/");

        if (data.length >= 1) {
            FillRequest request = new FillRequest(data[0]);
            if (data.length == 2) {
                request.setGenerations(Integer.parseInt(data[1]));
            }
            return request;
        }

        return null;
    }
}
