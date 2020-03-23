package _handler;

import _model.AuthToken;
import _request.AllPersonsRequest;
import _result.AllPersonsResult;
import _service.AllPersonsService;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;

public class AllPersonsHandler implements Handler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Gson gson = new Gson();
            AllPersonsResult allPersonsResult;
            if (exchange.getRequestMethod().toUpperCase().equals("GET")) {
                Headers reqHeaders = exchange.getRequestHeaders();
                if (reqHeaders.containsKey("Authorization")) {
                    String token = reqHeaders.getFirst("Authorization");
                    if (Handler.authTokenExists(token)) {
                        AuthToken authToken = Handler.getAuthToken(token);
                        // Extract the JSON string from the HTTP request body
                        AllPersonsRequest request = new AllPersonsRequest(authToken.getUserName(), authToken.getToken());

                        AllPersonsService service = new AllPersonsService();
                        allPersonsResult = service.fetch(request);

                        // Start sending the HTTP response to the client, starting with
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    } else {
                        // The auth token was invalid somehow, so we return a "not authorized"
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                        allPersonsResult = new AllPersonsResult("Error: invalid AuthToken");
                    }
                } else {
                    // We did not get an auth token.
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_UNAUTHORIZED, 0);
                    allPersonsResult = new AllPersonsResult("Error: No authorization header");
                }
            } else {
                // We expected a GET but got something else.
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                allPersonsResult = new AllPersonsResult("Error: bad request");
            }

            String respJson = gson.toJson(allPersonsResult);
            OutputStream respBody = exchange.getResponseBody();
            Handler.writeString(respJson, respBody);
            respBody.close();

        } catch (Exception e) {
            // Some kind of internal error has occurred inside the server.
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);

            // We are not sending a response body, so close the response body
            // output stream, indicating that the response is complete.
            exchange.getResponseBody().close();
            logger.log(Level.SEVERE, "Internal server error", e);
        }
    }
}
