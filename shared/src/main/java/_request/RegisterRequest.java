package _request;

import _model.User;
import java.util.UUID;

public class RegisterRequest {

    User user;

    /**
     * Create user from given data and generate a random PersonID
     * @param username username selected by user
     * @param password password selected by user
     * @param email user email
     * @param firstName user last name
     * @param lastName user last name
     * @param gender user gender ('m' or 'f')
     */
    public RegisterRequest(String username, String password, String email, String firstName, String lastName, String gender) {
        // Generate personID
        String personID = UUID.randomUUID().toString();
        this.user = new User(username, password, email, firstName, lastName, gender, personID);
    }

    /**
     * Assign new user and generate PersonID
     * @param user User model object
     */
    public RegisterRequest(User user) {
        String personID = UUID.randomUUID().toString();
        this.user = user;
        this.user.setPersonID(personID);
    }

    public User getUser() {
        return user;
    }

    public void setUserName(String userName) {
        this.user.setUserName(userName);
    }

    public void setPassword(String password) {
        this.user.setPassword(password);
    }

    public void setEmail(String email) {
        this.user.setEmail(email);
    }

    public void setFirstName(String firstName) {
        this.user.setFirstName(firstName);
    }

    public void setLastName(String lastName) {
        this.user.setLastName(lastName);
    }

    public void setGender(String gender) {
        this.user.setGender(gender);
    }
}
