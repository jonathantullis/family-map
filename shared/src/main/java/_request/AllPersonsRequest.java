package _request;

public class AllPersonsRequest {
    /**
     * AuthToken of user
     */
    String authToken;

    /**
     * Username of user
     */
    String userName;

    /**
     * @param authToken AuthToken of user
     */
    public AllPersonsRequest(String userName, String authToken) {
        this.userName = userName;
        this.authToken = authToken;
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
}
