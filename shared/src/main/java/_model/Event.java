package _model;

import java.util.Comparator;

public class Event {
    /**
     * event ID
     */
    private String eventID;
    /**
     * username of user
     */
    private String associatedUsername;
    /**
     * id of person to whom the event is related
     */
    private String personID;
    /**
     * latitude coordinate of event
     */
    private Double latitude;
    /**
     * longitude coordinate of event
     */
    private Double longitude;
    /**
     * country of event
     */
    private String country;
    /**
     * city of event
     */
    private String city;
    /**
     * description of event
     */
    private String eventType;
    /**
     * year event took place
     */
    private Integer year;

    /**
     * Constructor to generate event
     * @param eventID ID of Event
     * @param associatedUsername Username associated with Event
     * @param personID Person to whom the event belongs
     * @param latitude Latitude of event location
     * @param longitude Longitude of event location
     * @param country Country of event location
     * @param city City of event location
     * @param eventType Description of event
     * @param year Year event took place
     */
    public Event(String eventID, String associatedUsername, String personID, Double latitude, Double longitude,
                 String country, String city, String eventType, int year) {
        this.eventID = eventID;
        this.associatedUsername = associatedUsername;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }

    public static boolean isEqual(Event a, Event b) {
        return a.getEventID().equals(b.getEventID()) &&
                a.getAssociatedUsername().equals(b.getAssociatedUsername()) &&
                a.getPersonID().equals(b.getPersonID()) &&
                a.getLatitude().equals(b.getLatitude()) &&
                a.getLongitude().equals(b.getLongitude()) &&
                a.getCountry().equals(b.getCountry()) &&
                a.getCity().equals(b.getCity()) &&
                a.getEventType().equals(b.getEventType()) &&
                a.getClass().equals(b.getClass());
    }

    public static class YearComparator implements Comparator<Event> {
        @Override
        public int compare(Event o1, Event o2) {
            return o1.getYear().compareTo(o2.getYear());
        }
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
