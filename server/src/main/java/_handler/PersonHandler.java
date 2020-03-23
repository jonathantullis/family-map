package _handler;

import _model.AuthToken;
import _request.PersonRequest;
import _result.PersonResult;
import _service.PersonService;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;

public class PersonHandler implements Handler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Gson gson = new Gson();
            PersonResult personResult;
            if (exchange.getRequestMethod().toUpperCase().equals("GET")) {
                Headers reqHeaders = exchange.getRequestHeaders();
                if (reqHeaders.containsKey("Authorization")) {
                    String token = reqHeaders.getFirst("Authorization");
                    if (Handler.authTokenExists(token)) {
                        // Verify that the user making the request isn't using someone else's authToken
                        AuthToken authToken = Handler.getAuthToken(token);

                        String personId = Handler.getSecondIdentifier(exchange.getRequestURI());
                        assert (personId != null);
                        PersonRequest request = new PersonRequest(personId, authToken.getUserName(), authToken.getToken());

                        PersonService service = new PersonService();
                        personResult = service.fetch(request);

                        // Check that the associated username matches the username of the authToken
                        if (personResult.getAssociatedUsername().equals(authToken.getUserName())) {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        } else {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                            personResult = new PersonResult("Error: invalid AuthToken");
                        }
                    } else {
                        // The auth token was invalid somehow, so we return a "not authorized"
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                        personResult = new PersonResult("Error: invalid AuthToken");
                    }
                } else {
                    // We did not get an auth token.
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_UNAUTHORIZED, 0);
                    personResult = new PersonResult("Error: No authorization header");
                }
            } else {
                // We expected a GET but got something else.
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                personResult = new PersonResult("Error: bad request");
            }

            String respJson = gson.toJson(personResult);
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
