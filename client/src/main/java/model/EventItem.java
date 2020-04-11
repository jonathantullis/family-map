package model;

public class EventItem {
    private final String eventType;
    private final String city;
    private final String country;
    private final String year;
    private final String personName;

    public EventItem(String eventType, String city, String country, String year, String personName) {
        this.eventType = eventType;
        this.city = city;
        this.country = country;
        this.year = year;
        this.personName = personName;
    }

    public String getEventType() {
        return eventType;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getYear() {
        return year;
    }

    public String getPersonName() {
        return personName;
    }
}