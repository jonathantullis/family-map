package model;

public class PersonItem {
    private String personID;
    private String name;
    private String relationship;
    private String gender;

    public PersonItem(String personID, String name, String relationship, String gender) {
        this.personID = personID;
        this.name = name;
        this.relationship = relationship;
        this.gender = gender;
    }

    public String getPersonID() {
        return personID;
    }

    public String getName() {
        return name;
    }

    public String getRelationship() {
        return relationship;
    }

    public String getGender() {
        return gender;
    }
}
