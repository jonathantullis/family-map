package client;

import java.util.ArrayList;

import _model.Event;
import _result.AllEventsResult;
import _result.AllPersonsResult;

public class DataCache {
    private String authToken;
    private String userName;
    private String userPersonId;
    private AllPersonsResult allPersonsResult;
    private AllEventsResult allEventsResult;
    private ArrayList<Event> allEventsFiltered;

    /************  Singleton  **************/
    private static DataCache instance;
    public static DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }
        return instance;
    }
    private DataCache() {}
    /****************************************/

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserPersonId() {
        return userPersonId;
    }

    public void setUserPersonId(String userPersonId) {
        this.userPersonId = userPersonId;
    }

    public AllPersonsResult getAllPersonsResult() {
        return allPersonsResult;
    }

    public void setAllPersonsResult(AllPersonsResult allPersonsResult) {
        this.allPersonsResult = allPersonsResult;
    }

    public static void setInstance(DataCache instance) {
        DataCache.instance = instance;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public AllEventsResult getAllEventsResult() {
        return allEventsResult;
    }

    public void setAllEventsResult(AllEventsResult allEventsResult) {
        this.allEventsResult = allEventsResult;
    }

    public ArrayList<Event> getAllEventsFiltered() {
        return allEventsFiltered;
    }

    public void setAllEventsFiltered(ArrayList<Event> allEventsFiltered) {
        this.allEventsFiltered = allEventsFiltered;
    }
}
