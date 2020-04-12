package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import _model.Event;
import _result.AllEventsResult;
import _result.AllPersonsResult;
import model.EventItem;

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

    public void invalidateData() {
        this._authToken = null;
        this. _userName = null;
        this._userPersonId = null;
        this._allPersonsResult = null;
        this._allEventsResult = null;
        this._allEventsFiltered = null;
        this._settings = new Settings();
    }

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
        Collections.sort(this._allEventsResult.getData(), new YearComparator());
    }

    public ArrayList<Event> allEventsFiltered() {
        return _allEventsFiltered;
    }

    public void setAllEventsFiltered(ArrayList<Event> allEventsFiltered) {
        this._allEventsFiltered = allEventsFiltered;
        Collections.sort(this._allEventsFiltered, new YearComparator());
    }

    public class YearComparator implements Comparator<Event> {
        @Override
        public int compare(Event o1, Event o2) {
            return o1.getYear().compareTo(o2.getYear());
        }
    }

    public Settings settings() {
        return _settings;
    }
}
