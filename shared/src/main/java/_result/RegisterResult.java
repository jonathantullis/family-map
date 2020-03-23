package _result;

public class RegisterResult extends Result {
    /**
     * authToken for current user session
     */
    private String authToken;
    /**
     * Username of user
     */
    private String userName;
    /**
     * person ID of user
     */
    private String personID;

    /**
     * Result of successful Register Request
     * @param authToken authToken for current user session
     * @param personID person ID of user
     */
    public RegisterResult(String userName, String authToken, String personID) {
        super(true, null);
        this.userName = userName;
        this.authToken = authToken;
        this.personID = personID;
    }

    /**
     * Result of failed Register Request
     * @param errorMessage description of Register Request error
     */
    public RegisterResult(String errorMessage) {
        super(false, errorMessage);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }
}
