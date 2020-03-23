package _request;

public class FillRequest {
    /**
     * Username of user
     */
    private String username;
    /**
     * The number of generations to be generated
     */
    private int generations;

    /**
     * Set all local data members to parameter values
     * @param username Username of user
     * @param generations The number of generations to be generated
     */
    public FillRequest(String username, int generations) {
        this.username = username;
        this.generations = generations;
    }

    /**
     * Set local username to parameter value. Set generations to default value of 4.
     * @param username Username of user
     */
    public FillRequest(String username) {
        this.username = username;
        this.generations = 4;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGenerations() {
        return generations;
    }

    public void setGenerations(int generations) {
        this.generations = generations;
    }
}
