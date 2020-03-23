package _model;

public class AuthToken {
    /**
     * authToken for user
     */
    private String token;
    /**
     * username of user
     */
    private String userName;

    /**
     * Both parameters are required and must not be null
     * @param token authToken for user
     * @param userName username of user
     */
    public AuthToken(String token, String userName) {
        this.token = token;
        this.userName = userName;
    }

    public static boolean isEqual(AuthToken a, AuthToken b) {
        return a.getToken().equals(b.getToken()) &&
                a.getUserName().equals(b.getUserName()) &&
                a.getClass().equals(b.getClass());
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
