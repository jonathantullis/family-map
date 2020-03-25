package _request;

public class LoginRequest {
    /**
     * username input by user
     */
    private String userName;
    /**
     * password input by user
     */
    private String password;

    /**
     * Set all local data members to parameter values
     * @param userName username input by user
     * @param password password input by user
     */
    public LoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
