package _model;

public class Person {
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
     *
     * @param personID unique id of person
     * @param associatedUsername username of user who is associated with this person
     * @param firstName first name of person
     * @param lastName last name of person
     * @param gender gender of person ('m' or 'f')
     * @param fatherID ID for father of person
     * @param motherID ID for mother of person
     * @param spouseID ID for spouse of person
     */
    public Person(String personID, String associatedUsername, String firstName, String lastName,
                  String gender, String fatherID, String motherID, String spouseID) {
        this.personID = personID;
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
    }

    public Person(String personID, String associatedUsername, String firstName, String lastName, String gender) {
        this.personID = personID;
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public static boolean isEqual(Person a, Person b) {
        return a.getPersonID().equals(b.getPersonID()) &&
                a.getAssociatedUsername().equals(b.getAssociatedUsername()) &&
                a.getFirstName().equals(b.getFirstName()) &&
                a.getLastName().equals(b.getLastName()) &&
                a.getGender().equals(b.getGender()) &&
                a.getFatherID().equals(b.getFatherID()) &&
                a.getMotherID().equals(b.getMotherID()) &&
                a.getSpouseID().equals(b.getSpouseID()) &&
                a.getClass().equals(b.getClass());
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
