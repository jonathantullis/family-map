package _request;

public class EventRequest {
    /**
     * EventID of desired Event
     */
    private String eventID;
    /**
     * AuthToken of user
     */
    private String authToken;

    /**
     * Username of user
     */
    private String userName;

    /**
     * Set eventID and authToken to given values
     *
     * @param eventID EventID of desired Event
     * @param authToken AuthToken of user
     */
    public EventRequest(String eventID, String userName, String authToken) {
        this.eventID = eventID;
        this.userName = userName;
        this.authToken = authToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
