package _model;

public class User {
    /**
     * username selected by user
     */
    private String userName;
    /**
     * password selected by user
     */
    private String password;
    /**
     * user email
     */
    private String email;
    /**
     * user last name
     */
    private String firstName;
    /**
     * user last name
     */
    private String lastName;
    /**
     * user gender ('m' or 'f')
     */
    private String gender;
    /**
     * user person ID
     */
    private String personID;

    /**
     *
     * @param userName username selected by user
     * @param password password selected by user
     * @param email user email
     * @param firstName user last name
     * @param lastName user last name
     * @param gender user gender ('m' or 'f')
     * @param personID user person ID
     */
    public User(String userName, String password, String email, String firstName, String lastName, String gender, String personID) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.personID = personID;
    }

    public static boolean isEqual(User a, User b) {
        return a.getUserName().equals(b.getUserName()) &&
                a.getPassword().equals(b.getPassword()) &&
                a.getEmail().equals(b.getEmail()) &&
                a.getFirstName().equals(b.getFirstName()) &&
                a.getLastName().equals(b.getLastName()) &&
                a.getGender().equals(b.getGender()) &&
                a.getPersonID().equals(b.getPersonID()) &&
                a.getClass().equals(b.getClass());
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }
}
