package _handler;

import _request.LoadRequest;
import _result.LoadResult;
import _service.LoadService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;

public class LoadHandler implements Handler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {

                // Parse the request body and create user object
                Gson gson = new Gson();
                InputStream reqBody = exchange.getRequestBody();
                String json = Handler.readString(reqBody);
                LoadRequest loadRequest = gson.fromJson(json, LoadRequest.class);
                assert (loadRequest != null);

                // Register the user in the Family Map database
                LoadService loadService = new LoadService();
                LoadResult result = loadService.load(loadRequest);

                // Start sending the HTTP response to the client
                if (result.isSuccess()) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                } else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                }

                // Parse result object into json string
                json = gson.toJson(result);
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
