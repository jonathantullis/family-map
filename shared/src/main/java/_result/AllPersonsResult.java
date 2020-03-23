package _result;

import _model.Person;

import java.util.ArrayList;

public class AllPersonsResult extends Result {
    /**
     * Array of all Person model objects related to the user
     */
    private ArrayList<Person> data;

    /**
     * Result of successful request
     * @param data Array of all Person model objects related to the user
     */
    public AllPersonsResult(ArrayList<Person> data) {
        super(true, null);
        this.data = data;
    }

    /**
     * Result of failed request
     * @param errorMessage description of error message
     */
    public AllPersonsResult(String errorMessage) {
        super(false, errorMessage);
    }

    public ArrayList<Person> getData() {
        return data;
    }

    public void setData(ArrayList<Person> data) {
        this.data = data;
    }
}
