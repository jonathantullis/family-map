package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import _model.Event;
import _model.Person;
import _result.AllEventsResult;
import _result.AllPersonsResult;
import model.EventItem;

public class DataCache {
    private String _authToken;
    private String _userName;
    private String _userPersonId;
    private Event _selectedEvent;
    private ArrayList<Person> _allPersons = new ArrayList<>();
    private ArrayList<Event> _allEvents = new ArrayList<>();
    private ArrayList<Event> _allEventsFiltered = new ArrayList<>();
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
        this._selectedEvent = null;
        this._allPersons = new ArrayList<>();
        this._allEvents = new ArrayList<>();
        this._allEventsFiltered = new ArrayList<>();
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

    public ArrayList<Person> allPersons() {
        return _allPersons;
    }

    public void setAllPersons(ArrayList<Person> allPersons) {
        this._allPersons = allPersons;
    }

    public String userName() {
        return _userName;
    }

    public void setUserName(String userName) {
        this._userName = userName;
    }

    public Event selectedEvent() {
        return _selectedEvent;
    }

    public void setSelectedEvent(Event _selectedEvent) {
        this._selectedEvent = _selectedEvent;
    }

    public ArrayList<Event> allEvents() {
        return _allEvents;
    }

    public void setAllEvents(ArrayList<Event> allEvents) {
        this._allEvents = allEvents;
    }

    public ArrayList<Event> allEventsFiltered() {
        return _allEventsFiltered;
    }

    public void setAllEventsFiltered(ArrayList<Event> allEventsFiltered) {
        this._allEventsFiltered = allEventsFiltered;
        Collections.sort(this._allEventsFiltered, new YearComparator());
    }

    public static class YearComparator implements Comparator<Event> {
        @Override
        public int compare(Event o1, Event o2) {
            return o1.getYear().compareTo(o2.getYear());
        }
    }

    public Settings settings() {
        return _settings;
    }
}
