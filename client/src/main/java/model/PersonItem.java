package model;

public class PersonItem {
    private String name;
    private String relationship;
    private String gender;

    public PersonItem(String name, String relationship, String gender) {
        this.name = name;
        this.relationship = relationship;
        this.gender = gender;
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
