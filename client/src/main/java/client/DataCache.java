package client;

import java.util.ArrayList;

import _model.Event;
import _result.AllEventsResult;
import _result.AllPersonsResult;

public class DataCache {
    private String _authToken;
    private String _userName;
    private String _userPersonId;
    private AllPersonsResult _allPersonsResult;
    private AllEventsResult _allEventsResult;
    private ArrayList<Event> _allEventsFiltered;
    private Settings _settings = new Settings();

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

    public String authToken() {
        return _authToken;
    }

    public void setAuthToken(String authToken) {
        this._authToken = authToken;
    }

    public String userPersonId() {
        return _userPersonId;
    }

    public void setUserPersonId(String userPersonId) {
        this._userPersonId = userPersonId;
    }

    public AllPersonsResult allPersonsResult() {
        return _allPersonsResult;
    }

    public void setAllPersonsResult(AllPersonsResult allPersonsResult) {
        this._allPersonsResult = allPersonsResult;
    }

    public String userName() {
        return _userName;
    }

    public void setUserName(String userName) {
        this._userName = userName;
    }

    public AllEventsResult allEventsResult() {
        return _allEventsResult;
    }

    public void setAllEventsResult(AllEventsResult allEventsResult) {
        this._allEventsResult = allEventsResult;
    }

    public ArrayList<Event> allEventsFiltered() {
        return _allEventsFiltered;
    }

    public void setAllEventsFiltered(ArrayList<Event> allEventsFiltered) {
        this._allEventsFiltered = allEventsFiltered;
    }

    public Settings settings() {
        return _settings;
    }
}
