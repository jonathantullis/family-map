package _result;

public class EventResult extends Result {
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
     * Result of successful request
     * @param eventID event ID
     * @param associatedUsername username of user
     * @param personID id of person to whom the event is related
     * @param latitude latitude coordinate of event
     * @param longitude longitude coordinate of event
     * @param country country of event
     * @param city city of event
     * @param eventType description of event
     * @param year year event took place
     */
    public EventResult(String eventID, String associatedUsername, String personID, Double latitude,
                       Double longitude, String country, String city, String eventType, int year) {
        super(true, null);
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

    /**
     * Result of failed request
     * @param errorMessage description of error
     */
    public EventResult(String errorMessage) {
        super(false, errorMessage);
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
