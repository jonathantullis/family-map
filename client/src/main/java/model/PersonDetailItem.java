package model;

public class PersonDetailItem {
    private final String detail;
    private final String description;

    public PersonDetailItem(String detail, String description) {
        this.detail = detail;
        this.description = description;
    }

    public String getDetail() {
        return detail;
    }

    public String getDescription() {
        return description;
    }
}
