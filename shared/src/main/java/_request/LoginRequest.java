package _request;

public class LoginRequest {
    /**
     * username input by user
     */
    private String username;
    /**
     * password input by user
     */
    private String password;

    /**
     * Set all local data members to parameter values
     * @param username username input by user
     * @param password password input by user
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
