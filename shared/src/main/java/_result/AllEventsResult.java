package _result;

import _model.Event;

import java.util.ArrayList;

public class AllEventsResult extends Result {
    /**
     * Array of all Event model objects related to family members of the user
     */
    private ArrayList<Event> data;

    /**
     * Result of successful request
     * @param data Array of all Event model objects related to family members of the user
     */
    public AllEventsResult(ArrayList<Event> data) {
        super(true, null);
        this.data = data;
    }

    /**
     * Result of failed request
     * @param errorMessage description of error
     */
    public AllEventsResult(String errorMessage) {
        super(false, errorMessage);
    }

    public ArrayList<Event> getData() {
        return data;
    }

    public void setData(ArrayList<Event> data) {
        this.data = data;
    }
}
