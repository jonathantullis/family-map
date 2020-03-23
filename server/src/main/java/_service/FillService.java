package _service;

import _dao.*;
import _model.Event;
import _model.Person;
import _model.User;
import _request.FillRequest;
import _result.FillResult;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FillService {
    private static Logger logger = Logger.getLogger("FillService");
    private Database db;
    private Connection conn;

    /**
     * <p>The required "username" parameter must be a user already registered with the server. If there is
     * any data in the database already associated with the given user name, it is deleted.</p>
     *
     * <p>The optional “generations” parameter lets the caller specify the number of generations of ancestors
     * to be generated, and must be a non-negative integer (the default is 4, which results in 31 new
     * persons each with associated events).</p>
     *
     * STEPS
     * 1. Make a request to the User DAO to make sure the username is registered, if it is not, return an error.
     * 2. Erase all data (Persons and Events) associated with the given username
     * 3. Make a request to the Person DAO
     *      - Generate the specified number of generations worth of persons and associate each one with the given username.
     *
     * @param r FillRequest object containing <code>username</code> and <code>generations</code>
     * @return FillResult object containing boolean <code>success</code> and <code>message</code>
     */
    public FillResult fill(FillRequest r) {
        int numAddedEvents = 0;
        int numAddedPersons = 0;
        try {
            db = new Database();
            conn = db.getConnection();
            if (userExists(r.getUsername())) {
                deleteAssociatedData(r.getUsername());
            } else {
                db.closeConnection(false);
                return new FillResult(false, "User does not exist");
            }
            if (r.getGenerations() < 0) {
                db.closeConnection(false);
                return new FillResult(false, "Invalid number of generations provided.");
            }
            insertGenerations(r.getUsername(), r.getGenerations());
            numAddedEvents = numEvents(r.getUsername());
            numAddedPersons = numPersons(r.getUsername());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            logger.warning("Error occurred while accessing database");
            try {
                db.closeConnection(false);
            } catch(DataAccessException error) {
                logger.log(Level.SEVERE, "Error closing database connection", error);
            }
            return new FillResult(false, "An error occurred");
        }

        return new FillResult(true, "Successfully added " + numAddedPersons +
                " persons and " + numAddedEvents + " events to the database.");
    }

    private boolean userExists(String username) throws DataAccessException {
        UserDAO uDao = new UserDAO(conn);
        User user = uDao.find(username);
        return user != null;
    }

    private int numEvents(String username) throws DataAccessException {
        EventDAO eDao = new EventDAO(conn);
        return eDao.findAll(username).size();
    }

    private int numPersons(String username) throws DataAccessException {
        PersonDAO pDao = new PersonDAO(conn);
        return pDao.findAll(username).size();
    }

    private void deleteAssociatedData(String username) throws DataAccessException {
        PersonDAO pDao = new PersonDAO(conn);
        EventDAO eDao = new EventDAO(conn);

        pDao.deleteAll(username);
        eDao.deleteAll(username);
    }

    private void insertGenerations(String username, int generations) throws DataAccessException {
        UserDAO uDao = new UserDAO(conn);
        User user = uDao.find(username);

        Person userPerson = new Person(user.getPersonID(), user.getUserName(), user.getFirstName(),
                user.getLastName(), user.getGender());

        insertFamilyRecursive(userPerson, 0, generations);
    }

    private void insertFamilyRecursive(Person child, int currentGeneration, final int totalGenerations) throws DataAccessException {
        Family family = new Family(child);
        PersonDAO pDao = new PersonDAO(conn);
        if (currentGeneration == totalGenerations) {
            child.setMotherID(null);
            child.setFatherID(null);
        }
        pDao.insert(child);
        if (currentGeneration == 0) {
            insertChildEvents(family);
        }

        if (currentGeneration < totalGenerations) {
            insertParentEvents(family);
            insertFamilyRecursive(family.getFather(), currentGeneration + 1, totalGenerations);
            insertFamilyRecursive(family.getMother(), currentGeneration + 1, totalGenerations);
        }
    }

    private void insertChildEvents(Family family) throws DataAccessException {
        EventDAO eDao = new EventDAO(conn);
        for (Event event : family.getEvents()) {
            if (event.getPersonID().equals(family.getChild().getPersonID())) {
                eDao.insert(event);
            }
        }
    }

    private void insertParentEvents(Family family) throws DataAccessException {
        EventDAO eDao = new EventDAO(conn);
        for (Event event : family.getEvents()) {
            if (event.getPersonID().equals(family.getMother().getPersonID()) ||
                    event.getPersonID().equals(family.getFather().getPersonID())) {
                eDao.insert(event);
            }
        }
    }

    private class Family {
        private Person child;
        private Person father;
        private Person mother;
        private ArrayList<Event> events = new ArrayList<>();

        Family(Person child) {
            this.child = child;
            generateParents();
            this.child.setFatherID(father.getPersonID());
            this.child.setMotherID(mother.getPersonID());
            generateEvents();
        }

        // Change child father ID and mother ID. Change parents Spouse ID
        private void generateParents() {
            Random rand = new Random();
            Gson gson = new Gson();

            // Add a few names in case there's an issue reading the file
            String[] maleNames = null;
            String[] femaleNames = null;
            String[] surNames = null;

            try {
                String json = new Scanner(new File("server/json/mnames.json")).useDelimiter("\\A").next();
                maleNames = gson.fromJson(json, String[].class);

                json = new Scanner(new File("server/json/fnames.json")).useDelimiter("\\A").next();
                femaleNames = gson.fromJson(json, String[].class);

                json = new Scanner(new File("server/json/snames.json")).useDelimiter("\\A").next();
                surNames = gson.fromJson(json, String[].class);
            } catch (FileNotFoundException e) {
                    logger.log(Level.WARNING, "Could not read json files", e);
            }

            assert maleNames != null;
            assert surNames != null;
            assert femaleNames != null;
            String fatherName = maleNames[rand.nextInt(maleNames.length)];
            String motherName = femaleNames[rand.nextInt(femaleNames.length)];
            String maidenName = surNames[rand.nextInt(surNames.length)];

            father = new Person(UUID.randomUUID().toString(), child.getAssociatedUsername(), fatherName,
                    child.getLastName(), "m");
            mother = new Person(UUID.randomUUID().toString(), child.getAssociatedUsername(), motherName,
                    maidenName, "f");

            father.setSpouseID(mother.getPersonID());
            mother.setSpouseID(father.getPersonID());
        }

        private void generateEvents() {
            Random rand = new Random(); // FIXME use rand to assign default numbers?
            final int CURRENT_YEAR = 2020;
            final int DEFAULT_AGE = 22;
            final int DEFAULT_MARRIAGE_AGE = 25;
            final int YEARS_MARRIED_BEFORE_CHILDREN = 4;
            final int DEFAULT_DEATH_AGE = 85;
            EventDAO eDao = new EventDAO(FillService.this.conn);
            Gson gson = new Gson();
            Location[] locations = null;

            // See if the child already has a birth year assigned.
            // Only the very first person (the user) should not have one at this point.
            Event birthEvent = null;
            try {
                birthEvent = eDao.find(child.getPersonID(), "Birth");
            } catch (DataAccessException e) {
                logger.log(Level.SEVERE, "Error finding child birth event", e);
            }
            int childBirthYear = (birthEvent == null) ? CURRENT_YEAR - DEFAULT_AGE : birthEvent.getYear();

            // Assume data based on child birth year
            int parentMarriageYear = childBirthYear - YEARS_MARRIED_BEFORE_CHILDREN;
            int parentBirthYear = parentMarriageYear - DEFAULT_MARRIAGE_AGE;
            int parentDeathYear = parentBirthYear + DEFAULT_DEATH_AGE;

            // Get all possible locations from json file
            try {
                String json = new Scanner(new File("server/json/locations.json")).useDelimiter("\\A").next();
                locations = gson.fromJson(json, Location[].class);
            } catch (FileNotFoundException e) {
                logger.log(Level.SEVERE, "Unable to read locations json file", e);
            }

            // BIRTH EVENTS
            assert locations != null;
            Location location = locations[rand.nextInt(locations.length)];
            events.add(new Event(UUID.randomUUID().toString(), child.getAssociatedUsername(), child.getPersonID(),
                    location.getLatitude(), location.getLongitude(), location.getCountry(), location.getCity(),
                    "Birth", childBirthYear));

            location = locations[rand.nextInt(locations.length)];
            events.add(new Event(UUID.randomUUID().toString(), mother.getAssociatedUsername(), mother.getPersonID(),
                    location.getLatitude(), location.getLongitude(), location.getCountry(), location.getCity(),
                    "Birth", parentBirthYear));

            location = locations[rand.nextInt(locations.length)];
            events.add(new Event(UUID.randomUUID().toString(), father.getAssociatedUsername(), father.getPersonID(),
                    location.getLatitude(), location.getLongitude(), location.getCountry(), location.getCity(),
                    "Birth", parentBirthYear));

            // MARRIAGE EVENTS
            location = locations[rand.nextInt(locations.length)];
            events.add(new Event(UUID.randomUUID().toString(), father.getAssociatedUsername(), father.getPersonID(),
                    location.getLatitude(), location.getLongitude(), location.getCountry(), location.getCity(),
                    "Marriage", parentMarriageYear));
            events.add(new Event(UUID.randomUUID().toString(), mother.getAssociatedUsername(), mother.getPersonID(),
                    location.getLatitude(), location.getLongitude(), location.getCountry(), location.getCity(),
                    "Marriage", parentMarriageYear));

            // DEATH EVENTS
            location = locations[rand.nextInt(locations.length)];
            events.add(new Event(UUID.randomUUID().toString(), father.getAssociatedUsername(), father.getPersonID(),
                    location.getLatitude(), location.getLongitude(), location.getCountry(), location.getCity(),
                    "Death", parentDeathYear));
            events.add(new Event(UUID.randomUUID().toString(), mother.getAssociatedUsername(), mother.getPersonID(),
                    location.getLatitude(), location.getLongitude(), location.getCountry(), location.getCity(),
                    "Death", parentDeathYear));

        }

        private ArrayList<Event> getEvents() {
            return events;
        }

        public Person getChild() {
            return child;
        }

        public Person getFather() {
            return father;
        }

        public Person getMother() {
            return mother;
        }
    }

    private static class Location {
        private String country;
        private String city;
        private double latitude;
        private double longitude;

        public Location(String country, String city, double latitude, double longitude) {
            this.country = country;
            this.city = city;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getCountry() {
            return country;
        }

        public String getCity() {
            return city;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }
}
