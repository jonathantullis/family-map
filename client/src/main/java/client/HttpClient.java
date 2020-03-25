package client;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import _request.*;
import _result.*;

public class HttpClient {
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
                return gson.fromJson(json, RegisterResult.class);
            } else {
                // The response code indicated an error
                return new RegisterResult("Failed to Register");
            }
        } catch (IOException e) {
            System.out.println("There was an error");
            e.printStackTrace();
        }

        return null;
    }

    public LoginResult login(LoginRequest loginRequest) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);	// There is a request body
//            http.addRequestProperty("Authorization", "asdlfkjijew");
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
                return gson.fromJson(json, LoginResult.class);
            } else {
                // The response code indicated an error
                return new LoginResult("Failed to Sign In");
            }
        } catch (IOException e) {
            System.out.println("There was an error");
            e.printStackTrace();
        }

        return null;
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
