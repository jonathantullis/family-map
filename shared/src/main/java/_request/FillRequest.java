package _request;

public class FillRequest {
    /**
     * Username of user
     */
    private String userName;
    /**
     * The number of generations to be generated
     */
    private int generations;

    /**
     * Set all local data members to parameter values
     * @param userName Username of user
     * @param generations The number of generations to be generated
     */
    public FillRequest(String userName, int generations) {
        this.userName = userName;
        this.generations = generations;
    }

    /**
     * Set local username to parameter value. Set generations to default value of 4.
     * @param userName Username of user
     */
    public FillRequest(String userName) {
        this.userName = userName;
        this.generations = 4;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getGenerations() {
        return generations;
    }

    public void setGenerations(int generations) {
        this.generations = generations;
    }
}
