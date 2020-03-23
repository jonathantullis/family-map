package _result;

public class PersonResult extends Result {
    /**
     * unique id of person
     */
    private String personID;
    /**
     * username of user who is associated with this person
     */
    private String associatedUsername;
    /**
     * first name of person
     */
    private String firstName;
    /**
     * last name of person
     */
    private String lastName;
    /**
     * gender of person ('m' or 'f')
     */
    private String gender;
    /**
     * ID for father of person
     */
    private String fatherID;
    /**
     * ID for mother of person
     */
    private String motherID;
    /**
     * ID for spouse of person
     */
    private String spouseID;

    /**
     * Result of successful request
     * @param personID unique id of person
     * @param associatedUsername username of user who is associated with this person
     * @param firstName first name of person
     * @param lastName last name of person
     * @param gender gender of person ('m' or 'f')
     * @param fatherID ID for father of person
     * @param motherID ID for mother of person
     * @param spouseID ID for spouse of person
     */
    public PersonResult(String personID, String associatedUsername, String firstName, String lastName,
                        String gender, String fatherID, String motherID, String spouseID) {
        super(true, null);
        this.personID = personID;
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
    }

    /**
     * Result of failed request
     * @param errorMessage description of error
     */
    public PersonResult(String errorMessage) {
        super(false, errorMessage);
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
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

    public String getFatherID() {
        return fatherID;
    }

    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    public String getMotherID() {
        return motherID;
    }

    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    public String getSpouseID() {
        return spouseID;
    }

    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
    }
}
