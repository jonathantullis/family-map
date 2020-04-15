package com;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import _model.User;
import _request.AllEventsRequest;
import _request.AllPersonsRequest;
import _request.ClearRequest;
import _request.LoginRequest;
import _request.RegisterRequest;
import _result.AllEventsResult;
import _result.AllPersonsResult;
import _result.ClearResult;
import _result.LoginResult;
import _result.RegisterResult;
import client.Proxy;

import static org.junit.jupiter.api.Assertions.*;

public class ProxyTest {
    private final String serverHost = "192.168.1.124";
    private final String port = "8080";
    private Proxy proxy = Proxy.getInstance(serverHost, port);

    private final User TEST_USER = new User("jon", "password", "jon@gmail.com", "Jonny", "Tullis", "m", null);

    @BeforeEach
    public void setUp() {
        // Clear database before each test
        ClearResult result = proxy.clear(new ClearRequest());
        if (!result.isSuccess()) {
            System.out.println(result.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        // Clear database before each test
        ClearResult result = proxy.clear(new ClearRequest());
        if (!result.isSuccess()) {
            System.out.println(result.getMessage());
        }
    }

    @Test
    public void loginPass() {
        RegisterResult registerResult = proxy.register(new RegisterRequest(TEST_USER));
        assertTrue(registerResult.isSuccess(), registerResult.getMessage());

        LoginResult result = proxy.login(new LoginRequest(TEST_USER.getUserName(), TEST_USER.getPassword()));
        assertTrue(result.isSuccess(), "Expected successful login after user registered");
    }

    @Test
    public void loginFail() {
        // User not registered
        LoginResult result = proxy.login(new LoginRequest(TEST_USER.getUserName(), TEST_USER.getPassword()));
        assertFalse(result.isSuccess(), "Expected failed login for unregistered user");
    }

    @Test
    public void registerPass() {
        RegisterResult registerResult = proxy.register(new RegisterRequest(TEST_USER));
        assertTrue(registerResult.isSuccess(), registerResult.getMessage());
    }

    @Test
    public void registerFail() {
        // First registration should be successful
        RegisterResult registerResult = proxy.register(new RegisterRequest(TEST_USER));
        assertTrue(registerResult.isSuccess(), registerResult.getMessage());

        // Second attempt with same user should fail
        registerResult = proxy.register(new RegisterRequest(TEST_USER));
        assertFalse(registerResult.isSuccess(), "Expected failed register attempt for already registered user");
    }

    @Test
    public void retrievePeoplePass() {
        RegisterResult registerResult = proxy.register(new RegisterRequest(TEST_USER));
        assertTrue(registerResult.isSuccess(), registerResult.getMessage());

        AllPersonsResult allPersonsResult = proxy.fetchAllPersons(new AllPersonsRequest(TEST_USER.getUserName(), registerResult.getAuthToken()));
        assertTrue(allPersonsResult.isSuccess(), "Should successfully fetch all people for registered user");
        assertTrue(allPersonsResult.getData().size() > 0, "Data should not be empty. The server auto-generates family at each register");
    }

    @Test
    public void retrievePeopleFail() {
        // User is not registered
        AllPersonsResult allPersonsResult = proxy.fetchAllPersons(new AllPersonsRequest(TEST_USER.getUserName(), "FAKE_TOKEN"));
        assertFalse(allPersonsResult.isSuccess(), "Should fail if user is not registered");
        assertNull(allPersonsResult.getData(), "No data should be returned with failed request");

        RegisterResult registerResult = proxy.register(new RegisterRequest(TEST_USER));
        assertTrue(registerResult.isSuccess(), registerResult.getMessage());

        // User is registered, but AuthToken is wrong
        allPersonsResult = proxy.fetchAllPersons(new AllPersonsRequest(TEST_USER.getUserName(), "BAD_TOKEN123"));
        assertFalse(allPersonsResult.isSuccess(), "Should fail if auth token is bad");
        assertNull(allPersonsResult.getData(), "No data should be returned with failed request");
    }

    @Test
    public void retrieveEventsPass() {
        RegisterResult registerResult = proxy.register(new RegisterRequest(TEST_USER));
        assertTrue(registerResult.isSuccess(), registerResult.getMessage());

        AllEventsResult allEventsResult = proxy.fetchAllEvents(new AllEventsRequest(TEST_USER.getUserName(), registerResult.getAuthToken()));
        assertTrue(allEventsResult.isSuccess(), "Should successfully fetch all people for registered user");
        assertTrue(allEventsResult.getData().size() > 0, "Data should not be empty. The server auto-generates family at each register");
    }

    @Test
    public void retrieveEventsFail() {
        // User is not registered
        AllEventsResult allEventsResult = proxy.fetchAllEvents(new AllEventsRequest(TEST_USER.getUserName(), "FAKE_TOKEN"));
        assertFalse(allEventsResult.isSuccess(), "Should fail if user is not registered");
        assertNull(allEventsResult.getData(), "No data should be returned with failed request");

        RegisterResult registerResult = proxy.register(new RegisterRequest(TEST_USER));
        assertTrue(registerResult.isSuccess(), registerResult.getMessage());

        // User is registered, but AuthToken is wrong
        allEventsResult = proxy.fetchAllEvents(new AllEventsRequest(TEST_USER.getUserName(), "BAD_TOKEN123"));
        assertFalse(allEventsResult.isSuccess(), "Should fail if auth token is bad");
        assertNull(allEventsResult.getData(), "No data should be returned with failed request");
    }
}
