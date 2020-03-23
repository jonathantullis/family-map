package _handler;

import _model.User;
import _request.LoginRequest;
import _result.LoginResult;
import _service.LoginService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginHandler implements Handler {
    private static Logger logger = Logger.getLogger("LoginHandler");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {
                // Parse the request body and create user object
                User user = Handler.getUserFromRequestBody(exchange.getRequestBody());

                // Register the user in the Family Map database
                LoginService loginService = new LoginService();
                LoginResult result = loginService.login(new LoginRequest(user.getUserName(), user.getPassword()));

                if (result.isSuccess()) {
                    // Start sending the HTTP response to the client
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                } else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                }

                // Parse result object into json string
                Gson gson = new Gson();
                String json = gson.toJson(result);
                OutputStream respBody = exchange.getResponseBody();
                Handler.writeString(json, respBody);

                // Close the output stream
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
