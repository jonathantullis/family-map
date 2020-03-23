import _handler.*;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class Server {
    private static Logger logger = Logger.getLogger("Server");

    public static void main(String[] args) {
        int port;
        if (args.length > 1) {
            logger.severe("Invalid arguments.\nUsage: Server { port }");
            return;
        } else if (args.length == 1) {
            port = Integer.parseInt(args[0]);
            if (port < 1 || port > 65535) {
                logger.severe("Invalid port provided.");
                return;
            }
        } else {
            // Default port number if none is provided
            port = 8080;
        }

        try {
            Server.getInstance(port);
        } catch (Exception e) {
            logger.severe("Error starting server instance.");
        }
    }

    // Singleton
    private static Server instance;
    public static Server getInstance(int port) throws IOException {
        if (instance == null) {
            instance = new Server(port);
        }
        return instance;
    }

    private Server(int port) throws IOException {
        HttpServer httpServer;
        httpServer = HttpServer.create(new InetSocketAddress(port), 10);
        httpServer.createContext("/", new DefaultHandler());
        httpServer.createContext("/user/register", new RegisterHandler());
        httpServer.createContext("/user/login", new LoginHandler());
        httpServer.createContext("/clear", new ClearHandler());
        httpServer.createContext("/fill/", new FillHandler());
        httpServer.createContext("/load", new LoadHandler());
        httpServer.createContext("/person/", new PersonHandler());
        httpServer.createContext("/person", new AllPersonsHandler());
        httpServer.createContext("/event/", new EventHandler());
        httpServer.createContext("/event", new AllEventsHandler());

        httpServer.start();
        logger.info("Listening on port " + String.valueOf(port));
    }
}
