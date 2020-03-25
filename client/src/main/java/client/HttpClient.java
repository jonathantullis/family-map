package client;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import _model.AuthToken;
import _request.*;
import _result.*;

public class HttpClient {
    private DataCache dataCache = DataCache.getInstance();
    private String serverHost;
    private String serverPort;


    /************  Singleton  **************/
    private static HttpClient instance;
    public static HttpClient getInstance(String serverHost, String serverPort) {
        if (instance == null) {
            instance = new HttpClient(serverHost, serverPort);
        }
        return instance;
    }

    public static HttpClient getInstance() {
        return instance;
    }

    private HttpClient(String serverHost, String serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }
    /****************************************/

    public RegisterResult register(RegisterRequest registerRequest) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);	// There is a request body
            http.addRequestProperty("Accept", "application/json"); // We want json

            http.connect();

            // Send request to server
            Gson gson = new Gson();
            String json = gson.toJson(registerRequest.getUser());
            OutputStream reqBody = http.getOutputStream();
            writeString(json, reqBody);

            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                json = readString(respBody);
                RegisterResult result = gson.fromJson(json, RegisterResult.class);
                // Save user data and authToken for current session
                this.dataCache.setAuthToken(result.getAuthToken());
                this.dataCache.setUserName(registerRequest.getUser().getUserName());
                this.dataCache.setUserPersonId(result.getPersonID());
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // The response code indicated an error
        return new RegisterResult("Failed to Register");
    }

    public LoginResult login(LoginRequest loginRequest) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);	// There is a request body
            http.addRequestProperty("Accept", "application/json"); // We want json

            http.connect();

            // Send request to server
            Gson gson = new Gson();
            String json = gson.toJson(loginRequest);
            OutputStream reqBody = http.getOutputStream();
            writeString(json, reqBody);

            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                json = readString(respBody);
                LoginResult result = gson.fromJson(json, LoginResult.class);
                // Save user data and authToken for current session
                this.dataCache.setAuthToken(result.getAuthToken());
                this.dataCache.setUserName(loginRequest.getUserName());
                this.dataCache.setUserPersonId(result.getPersonID());
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // The response code indicated an error
        return new LoginResult("Failed to Sign In");
    }

    public AllPersonsResult fetchAllPersons(AllPersonsRequest allPersonsRequest) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);	// There is NO request body
            http.addRequestProperty("Authorization", dataCache.getAuthToken());
            http.addRequestProperty("Accept", "application/json"); // We want json

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                InputStream respBody = http.getInputStream();
                String json = readString(respBody);
                System.out.println("Successfully fetched related persons");
                this.dataCache.setAllPersonsResult(gson.fromJson(json, AllPersonsResult.class));
                return dataCache.getAllPersonsResult();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // The response code indicated an error
        return new AllPersonsResult("Error fetching all related persons");
    }

    public AllEventsResult fetchAllEvents(AllEventsRequest allEventsRequest) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);	// There is NO request body
            http.addRequestProperty("Authorization", dataCache.getAuthToken());
            http.addRequestProperty("Accept", "application/json"); // We want json

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                InputStream respBody = http.getInputStream();
                String json = readString(respBody);
                System.out.println("Successfully fetched related events");
                this.dataCache.setAllEventsResult(gson.fromJson(json, AllEventsResult.class));
                return dataCache.getAllEventsResult();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // The response code indicated an error
        return new AllEventsResult("Error fetching all related events");
    }

    /*
		The writeString method shows how to write a String to an OutputStream.
	*/
    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

    /*
		The readString method shows how to read a String from an InputStream.
	*/
    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    public String getServerHost() {
        return serverHost;
    }

    public String getServerPort() {
        return serverPort;
    }
}
