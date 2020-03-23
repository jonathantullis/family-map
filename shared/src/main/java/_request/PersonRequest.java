package _request;

public class PersonRequest {
    /**
     * PersonID of desired Person
     */
    private String personID;
    /**
     * AuthToken of user
     */
    private String authToken;

    /**
     * Username of user
     */
    private String userName;

    /**
     * Set personID and authToken to given values
     *
     * @param personID PersonID of desired Person
     * @param authToken AuthToken of user
     */
    public PersonRequest(String personID, String userName, String authToken) {
        this.personID = personID;
        this.userName = userName;
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }
}
