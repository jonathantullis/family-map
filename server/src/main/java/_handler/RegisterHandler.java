package _handler;

import _model.User;
import _request.RegisterRequest;
import _result.RegisterResult;
import _service.RegisterService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterHandler implements HttpHandler {
    private static Logger logger = Logger.getLogger("RegisterHandler");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {

                // Parse the request body and create user object
                User user = Handler.getUserFromRequestBody(exchange.getRequestBody());

                // Register the user in the Family Map database
                RegisterService registerService = new RegisterService();
                RegisterResult result = registerService.register(new RegisterRequest(user));

                // Start sending the HTTP response to the client
                if (result.isSuccess()) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                } else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                }

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
